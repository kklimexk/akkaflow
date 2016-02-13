package pl.edu.agh.workflow

import pl.edu.agh.flows.{In, Out}

abstract class AbstractWorkflow(name: String, numOfIns: Int, numOfOuts: Int) {

  var ins = {
    var insSeq = Seq.empty[In]
    for (i <- 0 until numOfIns) {
      insSeq :+= In(List.empty[Int])
    }
    insSeq
  }

  var outs = {
    var outsSeq = Seq.empty[Out]
    for (i <- 0 until numOfOuts) {
      outsSeq :+= Out(List.empty[Int])
    }
    outsSeq
  }

  override def toString = s"Workflow { \n\tname = $name, \n\tins = $ins, \n\touts = $outs \n}"
}
