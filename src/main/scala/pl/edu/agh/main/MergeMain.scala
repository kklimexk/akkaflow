package pl.edu.agh.main

import pl.edu.agh.actions.Action
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.flows.{Out, In, Source}
import pl.edu.agh.utils.ActorUtils._
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.workflow_patterns.merge.Merge

object MergeMain extends App {

  val action = Action[Int, Int] { in =>
    in
  }

  val firstProc = Merge (
    name = "sumProc",
    numOfOuts = 2,
    action = action,
    sendTo = "out0"
  )

  val secondProc = Merge (
    name = "sqrProc",
    numOfOuts = 2,
    action = action,
    sendTo = "out0"
  )

  val thirdProc = Merge (
    name = "thirdProc",
    numOfOuts = 2,
    action = action,
    sendTo = "out1"
  )

  val mergeProc = Merge (
    name = "mergeProc",
    numOfOuts = 1,
    action = action,
    sendTo = "out0"
  )

  val w = Workflow (
    name = "Merge example workflow",
    numOfIns = 3,
    numOfOuts = 1,
    (ins: Seq[In[Int]], outs: Seq[Out[Int]]) => {
      ins(0) ~>> firstProc
      ins(1) ~>> secondProc
      ins(2) ~>> thirdProc

      firstProc.outs(0) ~> mergeProc
      secondProc.outs(0) ~> mergeProc
      thirdProc.outs(1) ~> mergeProc

      mergeProc.outs(0) ~>> outs(0)
    }
  )

  Source(1 to 20) ~> w.ins(0)
  Source(30 to 50) ~> w.ins(1)
  Source(70 to 90) ~> w.ins(2)

  val res = w.run
  println(res)
  println(w)
}
