package pl.edu.agh.examples

import pl.edu.agh.actions.Outs
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.utils.ActorUtils.Implicits._
import pl.edu.agh.actions.ActionDsl.Implicits._
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.workflow.elements._
import pl.edu.agh.workflow_processes._

/**
  * Sequence Pattern Example
  */
object SequenceMain extends App {

  val sqr = { in: Int => implicit outs: Outs =>
    in * in =>> "out0"
  }

  val process1 = Process[Int, Int] (
    name = "process1",
    numOfOuts = 1,
    action = sqr
  )

  val process2 = Process[Int, Int] (
    name = "process2",
    numOfOuts = 1,
    action = sqr
  )

  val process3 = Process[Int, Int] (
    name = "process3",
    numOfOuts = 1,
    action = sqr
  )

  val w = Workflow (
    "Sequence Pattern Example",
    numOfIns = 1,
    numOfOuts = 1,
    (ins: Seq[In[Int]], outs: Seq[Out[Int]]) => {
      ins(0) ~>> process1
      process1.outs(0) ~> process2
      process2.outs(0) ~> process3
      process3.outs(0) ~>> outs(0)
    }
  )

  Source(1 to 10) ~> w.ins(0)

  val res = w.run
  println(res)
  println(w)
}
