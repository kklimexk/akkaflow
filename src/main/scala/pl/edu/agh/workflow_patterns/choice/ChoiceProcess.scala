package pl.edu.agh.workflow_patterns.choice

import akka.actor.ActorRef
import pl.edu.agh.flows.Sink
import pl.edu.agh.workflow_patterns.WorkflowProcess

abstract class ChoiceProcess[T, K](numOfOuts: Int) extends WorkflowProcess {

  protected var _outs = {
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
