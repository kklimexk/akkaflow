package pl.edu.agh.workflow

abstract class AbstractWorkflow(name: String) {
  var in = List.empty[Int]
  var out = List.empty[Int]

  override def toString = s"Workflow { \n\tname = $name, \n\tin = $in, \n\tout = $out \n}"
}
