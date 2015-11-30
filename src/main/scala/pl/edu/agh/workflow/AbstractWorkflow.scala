package pl.edu.agh.workflow

abstract class AbstractWorkflow {
  var name: String = _
  var in = List.empty[Int]
  var out = List.empty[Int]
}
