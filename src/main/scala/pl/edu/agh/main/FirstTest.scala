package pl.edu.agh.main

import pl.edu.agh.actions.Outs
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.flows.{In, Out, Source}
import pl.edu.agh.utils.ActorUtils.Implicits._
import pl.edu.agh.actions.ActionDsl._
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.workflow_patterns._

object FirstTest extends App {

  val sqr = { (in: Int, outs: Outs) =>
    in * in =>> outs("o1")
  }

  val sum = { (in: List[Int], outs: Outs) =>
    in.reduceLeft[Int](_+_) =>> outs("out0")
  }

  val sqrProc = Merge[Int, Int] (
    name = "sqrProc",
    outs = Seq("o1", "output2"),
    action = sqr
  )

  val sumProc = Merge[List[Int], Int] (
    name = "sumProc",
    numOfOuts = 2,
    action = sum
  )

  implicit val w = Workflow (
    "Sum of Squares workflow",
    (ins: Seq[In[Int]], outs: Seq[Out[Int]]) => {
      ins(0) ~>> sqrProc
      sqrProc.outs("o1").grouped(2) ~> sumProc
      sumProc.outs(0) ~>> outs(0)
    }
  )

  Source(1 to 4) ~> w.ins(0)
  Source(1 to 4) ~> w.ins(0)

  w.start

  Source(1 to 4) =>> w.ins(0)
  Source(1 to 4) =>> w.ins(0)

  //w.stop

  println(w.outs)
}
