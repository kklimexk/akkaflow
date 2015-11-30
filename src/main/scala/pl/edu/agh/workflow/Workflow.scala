package pl.edu.agh.workflow

import pl.edu.agh.utils.ActorUtils._

class Workflow(name: String, block: => (List[Int], List[Int]) => List[Int]) extends AbstractWorkflow with Runnable {
  def run: List[Int] = {
    val res = block(in, out)
    system.terminate
    res
  }
}

object Workflow {
  def apply(block: => (List[Int], List[Int]) => List[Int]) = new Workflow("", block)
  def apply(name: String, block: => (List[Int], List[Int]) => List[Int]) = new Workflow(name, block)
}
