package pl.edu.agh.main

import pl.edu.agh.actions.Action
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.flows.{Out, In, Source}
import pl.edu.agh.utils.ActorUtils.Implicits._
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.workflow_patterns.merge.Merge

object FirstTest extends App {

  val sqr = Action[Int, Int] { in =>
    in * in
  }

  val sum = Action[List[Int], Int] { in =>
    in.reduceLeft[Int](_+_)
  }

  val sqrProc = Merge (
    name = "sqrProc",
    numOfOuts = 2,
    action = sqr,
    sendTo = "out1"
  )

  val sumProc = Merge (
    name = "sumProc",
    numOfOuts = 2,
    action = sum,
    sendTo = "out0"
  )

  val w = Workflow (
    "Sum of Squares workflow",
    (ins: Seq[In[Int]], outs: Seq[Out[Int]]) => {
      ins(0) ~>> sqrProc
      sqrProc.outs(1).grouped(3) ~> sumProc
      sumProc.outs(0) ~>> outs(0)
    }
  )

  Source(1 to 6) ~> w.ins(0)
  val res = w.run
  println(res)
  println(w)

}
