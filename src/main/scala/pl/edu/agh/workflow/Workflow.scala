package pl.edu.agh.workflow

class Workflow(block: => () => List[Int]) {
  var name: String = ""
  var in = List.empty[Int]
  var out = List.empty[Int]

  def run = {
    block()
  }
}

object Workflow {
  def apply(block: () => List[Int]) = new Workflow(block)
}
