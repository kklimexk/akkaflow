package pl.edu.agh.main

import pl.edu.agh.actions.{Ins, Outs}
import pl.edu.agh.flows.{In, Out, Source}
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.utils.ActorUtils.Implicits._
import pl.edu.agh.actions.ActionDsl._
import pl.edu.agh.workflow_patterns._

object ApiTest extends App {

  val mergeProc = Merge[Int, Int] (
    name = "mergeProc",
    numOfOuts = 2,
    action = (in: Int, outs: Outs) => (in * in) =>> outs("out1")
  )

  val choiceProc = Choice[Int, Int] (
    name = "choiceProc",
    numOfOuts = 2,
    action = { (in: Int, outs: Outs) =>
      val i = in % outs().size
      in =>> outs(i)
    }
  )

  val multipleSyncProc = Sync[Int, Double] (
    name = "multipleSyncProc",
    numOfIns = 2,
    numOfOuts = 3,
    action = { (ins: Ins[Int], outs: Outs) =>
      ins("in1").toDouble =>> outs("out0")
      ins("in0").toDouble =>> outs("out1")
      "%.2f".format(ins(0).toDouble / ins("in1").toDouble).toDouble =>> outs("out2")
    }
  )

  val mergeProc2 = Merge[Double, String] (
    name = "mergeProc2",
    numOfOuts = 3,
    action = { in: Double => implicit outs: Outs => in.toString =>> "out1" }
  )

  val splitProc = Split[String, String] (
    name = "splitProc",
    outs = Seq("wyj1", "wyj2", "wyj3"),
    action = (in: String, outs: Outs) => outs().foreach(out => in =>> out)
  )

  val w = Workflow (
    "Api Test Workflow",
    numOfIns = 3,
    numOfOuts = 3,
    (ins: Seq[In[Int]], outs: Seq[Out[String]]) => {
      ins(0) ~>> mergeProc
      ins(1) ~>> mergeProc
      ins(2) ~>> mergeProc

      mergeProc.outs(1) ~> choiceProc

      choiceProc.outs(0) ~> multipleSyncProc
      choiceProc.outs(1) ~> multipleSyncProc

      multipleSyncProc.outs(0) ~> mergeProc2
      multipleSyncProc.outs("out1") ~> mergeProc2
      multipleSyncProc.outs(2) ~> mergeProc2

      mergeProc2.outs(1) ~> splitProc

      splitProc.outs("wyj1") ~>> outs(0)
      splitProc.outs("wyj2") ~>> outs(1)
      splitProc.outs("wyj3") ~>> outs(2)
    }
  )

  Source(1 to 3) ~> w.ins(0)
  Source(5 to 7) ~> w.ins(1)
  Source(9 to 11) ~> w.ins(2)

  val res = w.run
  println(res)
  println(w)

}
