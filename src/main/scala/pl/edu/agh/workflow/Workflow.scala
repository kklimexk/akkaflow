package pl.edu.agh.workflow

import pl.edu.agh.utils.ActorUtils._

class Workflow(name: String, block: => (List[Int], List[Int]) => List[Int]) extends AbstractWorkflow(name) with Runnable {
  def run: List[Int] = {
    out = block(in, out)
    system.terminate
    out
  }
}

object Workflow {
  def apply(block: => (List[Int], List[Int]) => List[Int]) = new Workflow("", block)
  def apply(name: String, block: => (List[Int], List[Int]) => List[Int]) = new Workflow(name, block)
}
