package pl.edu.agh.workflow

class Workflow(name: String, block: => (List[Int], List[Int]) => List[Int]) extends AbstractWorkflow with Runnable {
  def run: List[Int] = {
    block(in, out)
  }
}

object Workflow {
  def apply(block: => (List[Int], List[Int]) => List[Int]) = new Workflow("", block)
  def apply(name: String, block: => (List[Int], List[Int]) => List[Int]) = new Workflow(name, block)
}
