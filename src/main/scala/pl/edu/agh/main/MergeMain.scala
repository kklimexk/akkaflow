package pl.edu.agh.main

import pl.edu.agh.actions.Action
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.flows.{Out, In, Source}
import pl.edu.agh.utils.ActorUtils._
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.workflow_patterns.merge.Merge
import pl.edu.agh.workflow_patterns.synchronization._

object MergeMain extends App {

  val sum = Action[Int, Int] { in =>
    in + in
  }

  val sqr = Action[Int, Int] { in =>
    in * in * in * in
  }

  val mergeAct = Action[Int, Int] { in =>
    in
  }

  val sumProc = Sync (
    name = "sumProc",
    numOfOuts = 2,
    action = sum,
    sendTo = "out0"
  )

  val sqrProc = Sync (
    name = "sqrProc",
    numOfOuts = 2,
    action = sqr,
    sendTo = "out0"
  )

  val mergeProc = Merge (
    name = "mergeProc",
    numOfOuts = 1,
    action = mergeAct,
    sendTo = "out0"
  )

  val w = Workflow (
    name = "Merge example workflow",
    numOfIns = 2,
    numOfOuts = 1,
    (ins: Seq[In[Int]], outs: Seq[Out[Int]]) => {
      ins(0) ~>> sqrProc
      ins(1) ~>> sumProc

      sqrProc.outs(0) ~> mergeProc
      sumProc.outs(0) ~> mergeProc

      mergeProc.outs(0) ~>> outs(0)
    }
  )

  Source(1 to 4) ~> w.ins(0)
  Source(5 to 9) ~> w.ins(1)

  val res = w.run
  println(res)
  println(w)
}
