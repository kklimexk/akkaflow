package pl.edu.agh.main

import pl.edu.agh.actions.{Action3, Action}
import pl.edu.agh.flows._
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.workflow_patterns.merge.Merge
import pl.edu.agh.workflow_patterns.split.Split
import pl.edu.agh.utils.ActorUtils.Implicits._
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.workflow_patterns.synchronization.Sync

/** Prosty test majacy sprawdzic czy mozna uzyc roznych typow danych dla wejsc */
object AnyTypeInputTest extends App {

  val act = Action[Any, Any](identity)

  val sum = Action[List[Any], Any] { in =>
    val res = in match {
      case i: List[Double] => i.reduceLeft[Double](_+_)
      case i: List[Int] => i.reduceLeft[Int](_+_)
      case _ => identity(in)
    }
    res
  }

  val sumOnlyNumbers = Action3[Any, Any] { (in1, in2, in3) =>
    val res = (in1, in2, in3) match {
      case (i1: Int, i2: Int, i3: Int) => i1 + i2 + i3
      case (i1: Int, i2: Double, i3: Int) => i1 + i2 + i3
      case (i1: Int, i2: Int, i3: Double) => i1 + i2 + i3
      case (i1: Int, i2: String, i3: Double) => i1 + i3
      case _ => identity(in1, in2, in3)
    }
    res
  }

  val sumSyncProc = Sync (
    name = "sumProc",
    numOfOuts = 2,
    action = sumOnlyNumbers,
    sendTo = "out0"
  )

  val splitProc = Split (
    name = "splitProc",
    numOfOuts = 3,
    action = act
  )

  val mergeProc = Merge (
    name = "mergeProc",
    numOfOuts = 2,
    action = sum,
    sendTo = "out1"
  )

  val w = Workflow (
    "Any type input test",
    numOfIns = 3,
    numOfOuts = 3,
    (ins: Seq[In[Any]], outs: Seq[Out[Any]]) => {
      ins(0) ~>> sumSyncProc
      ins(1) ~>> sumSyncProc
      ins(2) ~>> sumSyncProc

      sumSyncProc.outs(0).grouped(10) ~> mergeProc

      mergeProc.outs(1) ~> splitProc

      splitProc.outs(0) ~>> outs(0)
      splitProc.outs(1) ~>> outs(1)
      splitProc.outs(2) ~>> outs(2)
    }
  )

  AnyRangeSource(1 to 10) ~> w.ins(0)
  AnySource("java", "scala", "akka", "drzewo", "lato", 1, 2, 3, 4, 5) ~> w.ins(1)
  AnySource(1.1, 2.2, 3.3, 4.4, 5.5, 6.6, 7.7, 8.8, 9.9, 100.0) ~> w.ins(2)

  val res = w.run
  println(res)
  println(w)
}
