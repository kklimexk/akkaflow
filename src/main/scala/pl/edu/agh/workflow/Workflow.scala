package pl.edu.agh.workflow

import pl.edu.agh.flows.{In, Out}
import pl.edu.agh.utils.ActorUtils._

class Workflow(name: String, numOfIns: Int, block: => (Seq[In], Out) => Out) extends AbstractWorkflow(name, numOfIns) with Runnable {
  def run: Out = {
    out = block(ins, out)
    system.terminate
    out
  }
}

object Workflow {
  def apply(block: => (Seq[In], Out) => Out) = new Workflow("", 1, block)
  def apply(name: String, block: => (Seq[In], Out) => Out) = new Workflow(name, 1, block)
  def apply(name: String, numOfIns: Int, block: => (Seq[In], Out) => Out) = new Workflow(name, numOfIns, block)
}
