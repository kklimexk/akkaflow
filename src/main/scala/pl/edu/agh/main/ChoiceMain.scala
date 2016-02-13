package pl.edu.agh.main

import pl.edu.agh.actions.Action
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.flows.{Out, In, Source}
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.workflow_patterns.choice.Choice
import pl.edu.agh.utils.ActorUtils._

object ChoiceMain extends App {

  val action = Action[Int, Int] { in =>
    in
  }

  val choiceProc = Choice[Int, Int] (
    action,
    in => (in > 0, in == 0, in < 0)
  )

  val w = Workflow (
    "Example Choice Workflow",
    numOfIns = 1,
    numOfOuts = 3,
    (ins: Seq[In[Int]], outs: Seq[Out[Int]]) => {
      ins(0) ~>> choiceProc
      choiceProc.out1 ~>> outs(0)
      choiceProc.out2 ~>> outs(2)
      choiceProc.out3 ~>> outs(1)
    }
  )

  Source(-10 to 10) ~> w.ins(0)
  val res = w.run
  println(res)
  println(w)

}
