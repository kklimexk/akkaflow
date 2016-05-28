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

/**
  * This is the Comet workflow, originally developed in the Kepler system.
  * The workflow requires "R" tool.
  */
object Comet extends App {

  val readDataSets = Sync[Any, List[(Map[String, Node], Map[String, Node])]] (
    name = "genXmlCollection",
    ins = Seq("xmlData", "config"),
    outs = Seq("stations:dataSetsCount"),
    action = { (ins: Ins[Any], outs: Outs) =>
      val xml = XML.loadFile(ins("xmlData").asInstanceOf[String])
      val config = ins("config").asInstanceOf[Map[String, Any]]
      val xpath = config("xpath").asInstanceOf[(String, String, String)]

      val xmlData = xml \\ xpath._1 filter (_ \ xpath._2 contains Text(xpath._3))

      var data = List.empty[(Map[String, Node], Map[String, Node])]
      xmlData.foreach { node =>
        val collectionName = node.attribute("name").get.head
        val res = (Map("collectionName" -> collectionName), Map("value" -> node))
        data :+= res
      }
      data =>> outs(0)
    }
  )

  val partitionData = Proc[List[(Map[String, Node], Map[String, Node])], (Array[Array[Array[Double]]], List[Node])] (
    name = "partitionData",
    outs = Seq("dataParts"),
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

      (data, cname) =>> outs("dataParts")
    }
  )

  val computeStats = Sync[Any, (List[(Map[String, Double], Map[String, Double], Map[String, Double], Map[String, Double])], List[Node])] (
    name = "computeStats",
    ins = Seq("dataParts", "config"),
    outs = Seq("stats"),
    action = { (ins: Ins[Any], outs: Outs) =>
      val cname = ins("dataParts").asInstanceOf[(Array[Array[Array[Double]]], List[Node])]._2
      val dsets = ins("dataParts").asInstanceOf[(Array[Array[Array[Double]]], List[Node])]._1(0)
      val config = ins("config").asInstanceOf[Map[String, Any]]
      val tBase = config("baseTemp").asInstanceOf[Int]
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

      (stats, cname) =>> outs("stats")
    }
  )

  val plotGraphs = Proc[(List[(Map[String, Double], Map[String, Double], Map[String, Double], Map[String, Double])], List[Node]), Unit] (
    name = "plotData",
    outs = Seq("graph"),
    action = { (stats: (List[(Map[String, Double], Map[String, Double], Map[String, Double], Map[String, Double])], List[Node]), outs: Outs) =>
      val dirPath = "./rlang/"
      val dir = new File(dirPath)
      if (!dir.exists()) dir.mkdirs()

      val filename = "plot-" + stats._2.mkString + "-" + new Date().getTime
      val rScript = "\n" +
        "data <- read.csv(\"" + dirPath + filename + ".csv\")\n" +
        "png(filename=\"" + dirPath + filename + ".png\")\n" +
        "with(data, plot(timestamp, min, type=\"l\", col=\"red\", ylab=\"\", ylim=c(0.0,100.0)))\n" +
        "with(data, lines(timestamp, max, type=\"l\", col=\"blue\"))\n" +
        "with(data, lines(timestamp, gdd, type=\"l\", col=\"green\"))\n" +
        "legend(\"topright\", legend=c(\"min\", \"max\", \"gdd\"), lty=1,col=c(\"red\", \"blue\", \"green\"), bty=\"n\", cex=.75)\n" +
        "x <- dev.off()"

      val file = new File(dirPath + filename + ".R")
      new PrintWriter(file) { write(rScript); close() }

      var data = "timestamp,min,max,gdd\n"

      stats._1.foreach { s =>
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
      println("All plots generated, exiting...");
      () =>> outs("emptyOut")
    }
  )

  val w = Workflow (
    name = "Comet example workflow",
    numOfIns = 2,
    numOfOuts = 1,
    (ins: Seq[In[Any]], outs: Seq[Out[Any]]) => {
      import pl.edu.agh.utils.ActorUtils.Implicits._

      ins(0) ~>> readDataSets
      ins(1) ~>> readDataSets

      readDataSets.outs(0) ~> partitionData

      partitionData.outs("dataParts") ~> computeStats
      ins(1) ~> computeStats

      computeStats.outs("stats") ~> plotGraphs

      plotGraphs.outs("graph") ~> collectPlots

      collectPlots.outs(0) ~>> outs(0)
    }
  )

  AnySource("./src/main/scala/pl/edu/agh/examples/comet/data.xml") ~> w.ins(0)
  AnySource(
    Map("xpath" -> ("Collection", "@label", "station"),
          "start_time" -> 1.196499599E9,
          "end_time" -> 1.197359999E9,
          "baseTemp" -> 10)
  ) ~> w.ins(1)

  val res = w.run
  println(res)
  println(w)
}
