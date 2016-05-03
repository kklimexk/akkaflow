package pl.edu.agh.workflow

import pl.edu.agh.flows.{In, Out}

abstract class AbstractWorkflow[T, R](name: String, numOfIns: Int, numOfOuts: Int) {

  var ins = {
    var insSeq = Seq.empty[In[T]]
    for (i <- 0 until numOfIns) {
      insSeq :+= In(List.empty[T])
    }
    insSeq
  }

  var outs = {
    var outsSeq = Seq.empty[Out[R]]
    for (i <- 0 until numOfOuts) {
      outsSeq :+= Out(List.empty[R])
    }
    outsSeq
  }

  def clearIns() = ins.foreach(in => in.data = List.empty[T])
  def clearOuts() = outs.foreach(out => out.result = List.empty[R])

  override def toString = s"Workflow { \n\tname = $name, \n\tins(latest) = $ins, \n\touts = $outs \n}"
}
