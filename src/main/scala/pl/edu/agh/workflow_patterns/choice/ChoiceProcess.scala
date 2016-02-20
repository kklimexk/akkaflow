package pl.edu.agh.workflow_patterns.choice

import akka.actor.ActorRef
import pl.edu.agh.flows.Sink
import pl.edu.agh.workflow_patterns.WorkflowProcess

abstract class ChoiceProcess[T, K](numOfIns: Int, numOfOuts: Int) extends WorkflowProcess {

  var ins = {
    var insSeq = Seq.empty[List[T]]
    for (i <- 0 until numOfIns) {
      insSeq :+= List.empty[T]
    }
    insSeq
  }

  var _outs = {
    var outsSeq = Seq.empty[ActorRef]
    for (i <- 0 until numOfOuts) {
      outsSeq :+= Sink[K]()
    }
    outsSeq
  }

  def outs = {
    _outs
  }

}
