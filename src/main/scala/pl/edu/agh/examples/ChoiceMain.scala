package pl.edu.agh.examples

import pl.edu.agh.actions.Outs
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.utils.Utils.crc32
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.utils.ActorUtils.Implicits._
import pl.edu.agh.actions.ActionDsl._
import pl.edu.agh.workflow.elements.{In, Out, StringSource}
import pl.edu.agh.workflow_processes._

object ChoiceMain extends App {

  val choiceAction = { (in: String, outs: Outs) =>
    val result = crc32(in)
    val i = result % outs().size
    in =>> outs(i)
  }

  val mergeAction = { (in: String, outs: Outs) =>
    in =>> outs("out1")
  }

  val choiceProc = Process[String, String] (
    name = "choice",
    numOfOuts = 3,
    action = choiceAction
  )

  val mergeProc = Process[String, String] (
    name = "merge",
    numOfOuts = 2,
    action = mergeAction
  )

  val w = Workflow (
    "Example Choice Workflow",
    numOfIns = 2,
    numOfOuts = 1,
    (ins: Seq[In[String]], outs: Seq[Out[String]]) => {
      ins(0) ~>> choiceProc
      ins(1) ~>> choiceProc

      choiceProc.outs(0) ~> mergeProc
      choiceProc.outs(1) ~> mergeProc
      choiceProc.outs(2) ~> mergeProc

      mergeProc.outs(1) ~>> outs(0)
    }
  )

  StringSource("ala", "pies", "mama", "telefon", "scala", "java", "obiad", "nauka", "agh") ~> w.ins(0)
  StringSource("jeden", "dwa", "trzy", "cztery", "piec", "szesc", "siedem", "osiem") ~> w.ins(1)

  val res = w.run
  println(res)
  println(w)

}
