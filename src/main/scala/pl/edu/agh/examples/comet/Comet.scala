package pl.edu.agh.examples.comet

import java.io.{File, PrintWriter}
import java.util.Date

import pl.edu.agh.actions.{Ins, Outs}
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.actions.ActionDsl.Implicits._
import pl.edu.agh.workflow.elements.{AnySource, In, Out}
import pl.edu.agh.workflow_processes.Sync
import pl.edu.agh.workflow_processes.{Process => Proc}

import scala.xml.{Node, Text, XML}
import sys.process._

object Comet extends App {

  val readDataSets = Sync[Any, List[(Map[String, Node], Map[String, Node])]] (
    name = "genXmlCollection",
    ins = Seq("xmlData", "config"),
    outs = Seq("stations:dataSetsCount"),
    action = { (ins: Ins[Any], outs: Outs) =>
      val xml = XML.loadFile(ins("xmlData").asInstanceOf[String])
      val config = ins("config").asInstanceOf[(Map[String, (String, String, String)], Map[String, Double], Map[String, Double], Map[String, Int])]

      val xmlData = xml \\ config._1("xpath")._1 filter (_ \ config._1("xpath")._2 contains Text(config._1("xpath")._3))

      var data = List.empty[(Map[String, Node], Map[String, Node])]
      xmlData.foreach { node =>
        val collectionName = node.attribute("name").get.head
        val res = (Map("collectionName" -> collectionName), Map("value" -> node))
        data :+= res
      }
      data =>> outs(0)
    }
  )

  val partitionData = Proc[List[(Map[String, Node], Map[String, Node])], Any] (
    name = "partitionData",
    outs = Seq("dataParts", "collectionName"),
    action = { (stations: List[(Map[String, Node], Map[String, Node])], outs: Outs) =>
      val pXmlData = stations.map(_._2("value"))
      val cname = stations.map(_._1("collectionName"))
      val nodes = pXmlData \\ "Collection" filter (_ \ "@label" contains Text("CollectionPoint"))

      var timeWindowLength = 43200; // 12 hours

      val data = Array.ofDim[Array[Array[Double]]](1)
      data(0) = Array[Array[Double]](Array.empty[Double])

      var t: Double = 0; var idx = 0; var first = true
      var timestamp, humidity, tref: Double = 0

      nodes.foreach { node =>
        if (t >= timeWindowLength) {
          t -= timeWindowLength; idx += 1; data(0) :+= Array.empty[Double]
          first = true
        }
        tref = timestamp

        val timestampPath = node \\ "Data" filter (_ \ "@label" contains Text("timestamps"))

        timestamp = timestampPath(0).text.toDouble
        if (first) { first = false; tref = timestamp; }
        val humidityPath = node \\ "Data" filter (_ \ "@label" contains Text("humidity"))
        humidity = humidityPath(0).text.toDouble
        t += timestamp - tref
        if (data(0)(idx).nonEmpty) {
          data(0)(idx) :+= timestamp
          data(0)(idx) :+= humidity
        } else {
          data(0)(idx) = Array(timestamp, humidity)
        }

      }

      cname =>> outs("collectionName")
      data =>> outs("dataParts")
    }
  )

  val computeStats = Sync[Any, String] (
    name = "computeStats",
    ins = Seq("dataParts", "collectionName", "config"),
    outs = Seq("stats", "collectionName"),
    action = { (ins: Ins[Any], outs: Outs) =>
      val config = ins("config").asInstanceOf[(Map[String, (String, String, String)], Map[String, Double], Map[String, Double], Map[String, Int])]
      val tBase = config._4("baseTemp")
      val dsets = ins("dataParts").asInstanceOf[Array[Array[Array[Double]]]](0)
      val cname = ins("collectionName").asInstanceOf[List[Node]]
      var stats = List.empty[(Map[String, Double], Map[String, Double], Map[String, Double], Map[String, Double])]
      dsets.foreach { d =>
        val t = d(0)
        var min: Double = 1; var max: Double = -1
        for (i <- 0 until d.length by 2) {
          if (min == -1 || min > d(i+1)) { min = d(i+1); }
          if (max == -1 || max < d(i+1)) { max = d(i+1); }
        }
        val gdd = if (max < tBase) 0 else (min + max) / 2 - tBase
        val res = (Map("timestamp" -> t), Map("min" -> min), Map("max" -> max), Map("gdd" -> gdd))
        stats :+= res
      }

      cname =>> outs("collectionName")
      stats =>> outs("stats")
    }
  )

  val plotGraphs = Sync[Any, Unit] (
    name = "plotData",
    ins = Seq("stats", "collectionName"),
    outs = Seq("graph"),
    action = { (ins: Ins[Any], outs: Outs) =>
      val dirPath = "./rlang/"
      val dir = new File(dirPath)
      if (!dir.exists()) dir.mkdirs()

      val cname = ins("collectionName").asInstanceOf[List[Node]]
      val filename = "plot-" + cname.toString() + "-" + new Date().getTime
      val rScript = "\n" +
        "data <- read.csv(\"" + filename + ".csv\")\n" +
        "png(filename=\"" + filename + ".png\")\n" +
        "with(data, plot(timestamp, min, type=\"l\", col=\"red\", ylab=\"\", ylim=c(0.0,100.0)))\n" +
        "with(data, lines(timestamp, max, type=\"l\", col=\"blue\"))\n" +
        "with(data, lines(timestamp, gdd, type=\"l\", col=\"green\"))\n" +
        "legend(\"topright\", legend=c(\"min\", \"max\", \"gdd\"), lty=1,col=c(\"red\", \"blue\", \"green\"), bty=\"n\", cex=.75)\n" +
        "x <- dev.off()"

      val file = new File(dirPath + filename + ".R")
      new PrintWriter(file) { write(rScript); close() }

      val stats = ins("stats").asInstanceOf[List[(Map[String, Double], Map[String, Double], Map[String, Double], Map[String, Double])]]
      var data = "timestamp,min,max,gdd\n"

      stats.foreach { s =>
        data += s._1("timestamp") + "," + s._2("min") + "," + s._3("max") + "," + s._4("gdd") + "\r\n"
      }

      val fileCsv = new File(dirPath + filename + ".csv")
      new PrintWriter(fileCsv) { write(data); close() }

      "R --vanilla -q -f " + dirPath + filename + ".R" !

      () =>> outs("graph")
    }
  )

  val collectPlots = Proc[Unit, Unit] (
    name = "collectGraphs",
    outs = Seq("emptyOut"),
    action = { (graph: Unit, outs: Outs) =>
      println("All plots generated, exiting...")
      () =>> outs("emptyOut")
    }
  )

  val w = Workflow (
    name = "Comet example workflow",
    numOfIns = 2,
    numOfOuts = 1,
    (ins: Seq[In[Any]], outs: Seq[Out[Unit]]) => {
      import pl.edu.agh.utils.ActorUtils.Implicits._

      ins(0) ~>> readDataSets
      ins(1) ~>> readDataSets

      readDataSets.outs(0) ~> partitionData

      partitionData.outs("dataParts") ~> computeStats
      partitionData.outs("collectionName") ~> computeStats
      ins(1) ~> computeStats

      computeStats.outs("stats") ~> plotGraphs
      computeStats.outs("collectionName") ~> plotGraphs

      plotGraphs.outs("graph") ~> collectPlots

      collectPlots.outs(0) ~>> outs(0)
    }
  )

  AnySource("./src/main/scala/pl/edu/agh/examples/comet/data.xml") ~> w.ins(0)
  AnySource(
    (Map("xpath" -> ("Collection", "@label", "station")), Map("start_time" -> 1.196499599E9),
      Map("end_time" -> 1.197359999E9), Map("baseTemp" -> 10))
  ) ~> w.ins(1)

  val res = w.run
  println(res)
  println(w)
}
