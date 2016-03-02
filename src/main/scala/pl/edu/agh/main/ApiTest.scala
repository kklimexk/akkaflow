package pl.edu.agh.main

import pl.edu.agh.actions.{Action2, Action}
import pl.edu.agh.flows.{Source, Out, In}
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.utils.ActorUtils._
import pl.edu.agh.workflow_patterns.choice.Choice
import pl.edu.agh.workflow_patterns.merge.Merge
import pl.edu.agh.workflow_patterns.synchronization.{Sync, MultipleSync}

object ApiTest extends App {

  val mergeAct = Action[Int, Int] { in =>
    in * in
  }

  val choiceAct = Action[Int, Int](identity)

  val multipleSyncAct = Action2[Int, Double] { (in0, in1) =>
    "%.2f".format(in0.toDouble / in1.toDouble).toDouble
  }

  val syncAct = Action[Double, String] { in =>
    in.toString
  }

  val mergeProc = Merge[Int, Int] (
    name = "mergeProc",
    numOfOuts = 2,
    action = mergeAct,
    sendTo = "out1"
  )

  val choiceProc = Choice[Int, Int] (
    name = "choiceProc",
    numOfOuts = 2,
    action = choiceAct,
    d => d
  )

  val multipleSyncProc = MultipleSync[Int, Double] (
    name = "multipleSyncProc",
    numOfOuts = 3,
    action = multipleSyncAct,
    sendTo = "out2"
  )

  val syncProc = Sync[Double, String] (
    name = "syncProc",
    numOfOuts = 3,
    action = syncAct,
    sendTo = "out1"
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

      multipleSyncProc.outs(2) ~> syncProc

      syncProc.outs(1) ~>> outs(2)
    }
  )

  Source(1 to 4) ~> w.ins(0)
  Source(5 to 8) ~> w.ins(1)
  Source(9 to 12) ~> w.ins(2)

  val res = w.run
  println(res)
  println(w)

}
