package pl.edu.agh.workflow

import pl.edu.agh.flows.{In, Out}
import pl.edu.agh.utils.ActorUtils._

class Workflow(name: String, block: => (In, Out) => Out) extends AbstractWorkflow(name) with Runnable {
  def run: Out = {
    out = block(in, out)
    system.terminate
    out
  }
}

object Workflow {
  def apply(block: => (In, Out) => Out) = new Workflow("", block)
  def apply(name: String, block: => (In, Out) => Out) = new Workflow(name, block)
}
