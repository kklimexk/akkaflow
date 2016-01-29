package pl.edu.agh.workflow

import pl.edu.agh.flows.{In, Out}

abstract class AbstractWorkflow(name: String, numOfIns: Int) {

  var ins = {
    var insSeq = Seq.empty[In]
    for (i <- 0 until numOfIns) {
      insSeq :+= In(List.empty[Int])
    }
    insSeq
  }

  var out = Out(List.empty[Int])

  override def toString = s"Workflow { \n\tname = $name, \n\tins = $ins, \n\tout = $out \n}"
}
