package pl.edu.agh.workflow

class Workflow(block: => (List[Int], List[Int]) => List[Int]) extends AbstractWorkflow with Runnable {
  def run: List[Int] = {
    block(in, out)
  }
}

object Workflow {
  def apply(block: => (List[Int], List[Int]) => List[Int]) = new Workflow(block)
}
