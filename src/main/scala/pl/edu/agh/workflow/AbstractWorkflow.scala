package pl.edu.agh.workflow

import pl.edu.agh.flows.{In, Out}

abstract class AbstractWorkflow(name: String) {
  var in = In(List.empty[Int])
  var out = Out(List.empty[Int])

  override def toString = s"Workflow { \n\tname = $name, \n\tin = $in, \n\tout = $out \n}"
}
