package pl.edu.agh.workflow

import pl.edu.agh.flows.{In, Out}
import pl.edu.agh.utils.ActorUtils._

class Workflow(name: String, numOfIns: Int, numOfOuts: Int, block: => (Seq[In], Seq[Out]) => Out) extends AbstractWorkflow(name, numOfIns, numOfOuts) with Runnable {
  def run: Seq[Out] = {
    block(ins, outs)
    system.terminate
    outs
  }
}

object Workflow {
  def apply(block: => (Seq[In], Seq[Out]) => Out) = new Workflow("", 1, 1, block)
  def apply(name: String, block: => (Seq[In], Seq[Out]) => Out) = new Workflow(name, 1, 1, block)
  def apply(name: String, numOfIns: Int, block: => (Seq[In], Seq[Out]) => Out) = new Workflow(name, numOfIns, 1, block)
  def apply(name: String, numOfIns: Int, numOfOuts: Int, block: => (Seq[In], Seq[Out]) => Out) = new Workflow(name, numOfIns, numOfOuts, block)
}
