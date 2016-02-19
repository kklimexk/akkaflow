package pl.edu.agh.main

import pl.edu.agh.actions.Action
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.flows._
import pl.edu.agh.utils.Utils.crc32
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.workflow_patterns.choice.Choice
import pl.edu.agh.utils.ActorUtils._

object ChoiceMain extends App {

  val action = Action[String, String] { in =>
    in
  }

  val choiceProc = Choice[String, String] (
    numOfIns = 1,
    numOfOuts = 3,
    action = action,
    d => crc32(d)
  )

  val w = Workflow (
    "Example Choice Workflow",
    numOfIns = 1,
    numOfOuts = 3,
    (ins: Seq[In[String]], outs: Seq[Out[String]]) => {
      ins(0) ~>> choiceProc
      choiceProc.outs(0) ~>> outs(0)
      choiceProc.outs(1) ~>> outs(1)
      choiceProc.outs(2) ~>> outs(2)
    }
  )

  StringSource("ala", "pies", "mama", "telefon", "scala", "java", "obiad", "nauka", "agh") ~> w.ins(0)
  val res = w.run
  println(res)
  println(w)

}
