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

  val sumProc = Sync {
    sum
  }

  val sqrProc = Sync {
    sqr
  }

  val mergeProc = Merge[Int]

  val w = Workflow (
    name = "Merge example workflow",
    numOfIns = 2,
    numOfOuts = 1,
    (ins: Seq[In[Int]], outs: Seq[Out[Int]]) => {
      ins(0) ~>> sqrProc
      ins(1) ~>> sumProc

      sqrProc.out ~> mergeProc
      sumProc.out ~> mergeProc

      mergeProc.out ~>> outs(0)
    }
  )

  Source(1 to 4) ~> w.ins(0)
  Source(5 to 9) ~> w.ins(1)

  val res = w.run
  println(res)
  println(w)
}
