package pl.edu.agh.examples

import scala.io.Source
import sys.process._
import java.net.URL
import java.io.{File, PrintWriter}

import pl.edu.agh.workflow.Workflow
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.actions.ActionDsl.Implicits._
import pl.edu.agh.actions.Outs
import pl.edu.agh.workflow.elements._
import pl.edu.agh.workflow_processes.{Process => Proc}

/**
  * This is the biological pathways workflow originally developed in the Taverna system.
  * Taverna implementation can be found here:
  * http://www.myexperiment.org/workflows/2673
  */
object Pathways extends App {

  val convertId = Proc[Int, String] (
    name = "convertId",
    outs = Seq("geneId"),
    action = { (gene: Int, outs: Outs) =>
      val geneId = Source.fromURL("http://rest.kegg.jp/conv/genes/ncbi-geneid:" + gene).mkString
        .trim
        .split("\t")(1)

      geneId =>> outs("geneId")
    }
  )

  val pathWayByGene = Proc[String, String] (
    name = "pathWayByGene",
    outs = ("pathId", "pathWayId"),
    action = { (geneId: String, outs: Outs) =>
      val body = Source.fromURL("http://rest.kegg.jp/link/pathway/" + geneId).mkString
      val pathId = body
        .trim
        .split("\t")(1)
        .split("path:")(1)

      pathId =>> outs("pathId")
      body =>> outs("pathWayId")
    }
  )

  val pathWayEntry = Proc[String, String] (
    name = "pathWayEntry",
    outs = Seq("pathwayEntry"),
    action = { (pathId: String, outs: Outs) =>
      val fileNameEntry = pathId + "-entry.txt"
      val pathwayEntry = Source.fromURL("http://rest.kegg.jp/get/" + pathId).mkString

      new PrintWriter("./pathway_entries/" + fileNameEntry) { write(pathwayEntry); close() }

      fileNameEntry =>> outs("pathwayEntry")
    }
  )

  val pathWayImage = Proc[String, String] (
    name = "pathWayImage",
    outs = Seq("pathwayImage"),
    action = { (pathId: String, outs: Outs) =>
      val fileNameImage = pathId + "-image.png"
      new URL("http://rest.kegg.jp/get/" + pathId + "/image") #> new File("./pathway_images/" + fileNameImage) !

      fileNameImage =>> outs("pathwayImage")
    }
  )

  val geneDescription = Proc[String, String] (
    name = "geneDescription",
    outs = Seq("geneDescription"),
    action = { (geneId: String, outs: Outs) =>
      val geneDescription = Source.fromURL("http://rest.kegg.jp/find/genes/" + geneId).mkString
        .trim
        .split("\t")(1)

      geneDescription =>> outs("geneDescription")
    }
  )

  val w = Workflow (
    name = "Pathways",
    numOfIns = 1,
    numOfOuts = 3,
    (ins: Seq[In[Int]], outs: Seq[Out[String]]) => {
      import pl.edu.agh.utils.ActorUtils.Implicits._

      ins(0) ~>> convertId

      convertId.outs("geneId") ~> pathWayByGene
      convertId.outs("geneId") ~> geneDescription

      pathWayByGene.outs("pathId") ~> pathWayEntry
      pathWayByGene.outs("pathId") ~> pathWayImage

      pathWayEntry.outs("pathwayEntry") ~>> outs(0)
      pathWayImage.outs("pathwayImage") ~>> outs(1)
      geneDescription.outs("geneDescription") ~>> outs(2)
    }
  )

  ParametrizedSource[Int](945003) ~> w.ins(0)

  val res = w.run
  println(res)
  println(w)
}
