package pl.edu.agh.examples

import pl.edu.agh.actions.Outs
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.utils.ActorUtils.Implicits._
import pl.edu.agh.actions.ActionDsl._
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.workflow.elements._
import pl.edu.agh.workflow_processes._

object SplitMain extends App {

  case class IntegerMessage(i: Int)

  val sqr = { (in: IntegerMessage, outs: Outs) =>
    outs().foreach(out => in.i * in.i =>> out)
  }

  val splitProc = Process[IntegerMessage, Int] (
    name = "splitProc",
    numOfOuts = 3,
    action = sqr
  )

  val w = Workflow (
    "Example split workflow",
    numOfIns = 1,
    numOfOuts = 3,
    (ins: Seq[In[IntegerMessage]], outs: Seq[Out[Int]]) => {
      ins(0) ~>> splitProc
      splitProc.outs(0) ~>> outs(0)
      splitProc.outs(1) ~>> outs(1)
      splitProc.outs(2) ~>> outs(2)
    }
  )

  ParametrizedSource[IntegerMessage]((1 to 10).map(IntegerMessage(_)):_*) ~> w.ins(0)

  val res = w.run
  println(res)
  println(w)
}
