package pl.edu.agh.examples

import pl.edu.agh.actions.{Action, Outs}
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.utils.ActorUtils.Implicits._
import pl.edu.agh.actions.ActionDsl._
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.workflow.elements.{In, Out, Source}
import pl.edu.agh.workflow_processes._

object MergeMain extends App {

  val firstAct = { (in: Int, outs: Outs) =>
    in =>> outs("out0")
  }

  val secondAct = { (in: Int, outs: Outs) =>
    in =>> outs("out1")
  }

  val firstProc = Process[Int, Int] (
    name = "sumProc",
    numOfOuts = 2,
    action = firstAct
  )

  val secondProc = Process[Int, Int] (
    name = "sqrProc",
    numOfOuts = 2,
    action = firstAct
  )

  val thirdProc = Process[Int, Int] (
    name = "thirdProc",
    numOfOuts = 2,
    action = secondAct
  )

  val mergeProc = Process[Int, Int] (
    name = "mergeProc",
    numOfOuts = 1,
    action = firstAct
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
