package pl.edu.agh.main

import pl.edu.agh.actions.Action
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.flows._
import pl.edu.agh.utils.Utils.crc32
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.workflow_patterns.choice.Choice
import pl.edu.agh.utils.ActorUtils.Implicits._
import pl.edu.agh.workflow_patterns.merge.Merge

object ChoiceMain extends App {

  val action = Action[String, String](identity)

  val choiceProc = Choice[String, String] (
    name = "choice",
    numOfOuts = 3,
    action = action,
    (d: String) => crc32(d)
  )

  val mergeProc = Merge[String, String] (
    name = "merge",
    numOfOuts = 2,
    action = action,
    sendTo = "out1"
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
