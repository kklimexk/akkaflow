package pl.edu.agh.workflow

import pl.edu.agh.flows.{In, Out}

abstract class AbstractWorkflow[T, K](name: String, numOfIns: Int, numOfOuts: Int) {

  var ins = {
    var insSeq = Seq.empty[In[T]]
    for (i <- 0 until numOfIns) {
      insSeq :+= In(List.empty[T])
    }
    insSeq
  }

  var outs = {
    var outsSeq = Seq.empty[Out[K]]
    for (i <- 0 until numOfOuts) {
      outsSeq :+= Out(List.empty[K])
    }
    outsSeq
  }

  override def toString = s"Workflow { \n\tname = $name, \n\tins = $ins, \n\touts = $outs \n}"
}
