package pl.edu.agh.main

import pl.edu.agh.actions.{Action, Outs}
import pl.edu.agh.flows.{In, Out, Source}
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.workflow_patterns.split.Split
import pl.edu.agh.utils.ActorUtils.Implicits._
import pl.edu.agh.actions.ActionDsl._
import pl.edu.agh.dsl.WorkFlowDsl._

object SplitMain extends App {

  val sqr = { (in: Int, outs: Outs) =>
    outs().foreach(out => in * in =>> out)
  }

  val splitProc = Split[Int, Int] (
    name = "splitProc",
    numOfOuts = 3,
    action = sqr
  )

  val w = Workflow (
    "Example split workflow",
    numOfIns = 1,
    numOfOuts = 3,
    (ins: Seq[In[Int]], outs: Seq[Out[Int]]) => {
      ins(0) ~>> splitProc
      splitProc.outs(0) ~>> outs(0)
      splitProc.outs(1) ~>> outs(1)
      splitProc.outs(2) ~>> outs(2)
    }
  )

  Source(1 to 10) ~> w.ins(0)
  val res = w.run
  println(res)
  println(w)
}
