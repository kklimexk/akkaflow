package pl.edu.agh.workflow

import pl.edu.agh.flows.{In, Out}

abstract class AbstractWorkflow(name: String) {

  var ins = Seq(In(List.empty[Int]), In(List.empty[Int]))

  var out = Out(List.empty[Int])

  override def toString = s"Workflow { \n\tname = $name, \n\tins = $ins, \n\tout = $out \n}"
}
