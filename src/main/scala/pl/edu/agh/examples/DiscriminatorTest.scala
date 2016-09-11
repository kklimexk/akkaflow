package pl.edu.agh.examples

import pl.edu.agh.actions.ActionDsl.Implicits._
import pl.edu.agh.actions.{Ins, Outs}
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.workflow.elements.{In, Out, Source}
import pl.edu.agh.workflow_processes.discriminator.Disc

// Discriminator Pattern example
object DiscriminatorTest extends App {

  val act = { (ins: Ins[Int], outs: Outs) =>

    //def removeIndex(idx: Int): List[Int] = inNames.zipWithIndex.filter(_._2 != idx).map(_._2)

    ins().foreach { in =>
      in._2 =>> outs(0)
    }
  }

  //n-out-of-m join (discriminator)
  val discriminatorProcess = Disc[Int, Int] (
    name = "discriminatorProcess",
    n = 2,
    m = 3,
    numOfOuts = 1,
    action = act
  )

  val w = Workflow (
    "Example discriminator workflow",
    numOfIns = 3,
    numOfOuts = 1,
    (ins: Seq[In[Int]], outs: Seq[Out[Int]]) => {
      import pl.edu.agh.utils.ActorUtils.Implicits._

      ins(0) ~>> discriminatorProcess
      ins(1) ~>> discriminatorProcess
      ins(2) ~>> discriminatorProcess

      discriminatorProcess.outs(0) ~>> outs(0)
    }
  )

  Source(1 to 10) ~> w.ins(0)
  Source(11 to 20) ~> w.ins(1)
  Source(21 to 30) ~> w.ins(2)

  val res = w.run
  println(res)
  println(w)
}
