package pl.edu.agh.examples

import pl.edu.agh.actions.{Ins, Outs}
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.actions.ActionDsl.Implicits._
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.workflow.elements.{In, Out, Source}
import pl.edu.agh.workflow_processes._
import pl.edu.agh.workflow_processes.synchronization.SyncDsl._

object SyncMain extends App {

  val sum = { (ins: Ins[Int], outs: Outs) =>
    ins(0) + ins(1) =>> outs("out0")
  }

  val mul = { (in: List[Int], outs: Outs) =>
    in.reduceLeft[Int](_*_) =>> outs("out0")
  }

  val sumProc = Sync[Int, Int]
    .name("sumProc")
    .numOfIns(2)
    .numOfOuts(2)
    .action(sum)

  val mulProc = Process[List[Int], Int] (
    name = "mulProc",
    numOfOuts = 1,
    action = mul
  )

  val w = Workflow (
    name = "Sum of two inputs and multiply every three of them",
    numOfIns = 2,
    numOfOuts = 1,
    (ins: Seq[In[Int]], outs: Seq[Out[Int]]) => {
      import pl.edu.agh.utils.ActorUtils.Implicits._

      ins(0) ~>> sumProc
      ins(1) ~>> sumProc
      sumProc.outs("out0").grouped(3) ~> mulProc
      mulProc.outs(0) ~>> outs(0)
    }
  )

  Source(1 to 6) ~> w.ins(0)
  Source(1 to 6) ~> w.ins(1)

  val res = w.run
  println(res)
  println(w)

}
