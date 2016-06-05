package pl.edu.agh.examples

import pl.edu.agh.actions.Outs
import pl.edu.agh.actions.Outs.Implicits._
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.utils.ActorUtils.Implicits._
import pl.edu.agh.actions.ActionDsl.Implicits._
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.workflow.elements._
import pl.edu.agh.workflow_processes._

import scala.util.Random

// Discriminator Pattern example
object DiscriminatorTest extends App {

  val sqr = { (in: Int, outs: Outs) =>
    val discOut = Random.nextInt(outs.size)
    outs().foreach { out =>
      if (outs(discOut) isNotEqualTo out) in * in =>> out
    }
  }

  val discriminatorProcess = Process[Int, Int] (
    name = "discriminatorProcess",
    numOfOuts = 3,
    action = sqr
  )

  val w = Workflow (
    "Example discriminator workflow",
    numOfIns = 2,
    numOfOuts = 3,
    (ins: Seq[In[Int]], outs: Seq[Out[Int]]) => {
      ins(0) ~>> discriminatorProcess
      ins(1) ~>> discriminatorProcess
      discriminatorProcess.outs(0) ~>> outs(0)
      discriminatorProcess.outs(1) ~>> outs(1)
      discriminatorProcess.outs(2) ~>> outs(2)
    }
  )

  Source(1 to 10) ~> w.ins(0)
  Source(11 to 20) ~> w.ins(1)

  val res = w.run
  println(res)
  println(w)
}
