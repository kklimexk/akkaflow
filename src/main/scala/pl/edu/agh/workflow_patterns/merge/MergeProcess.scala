package pl.edu.agh.workflow_patterns.merge

import akka.actor.ActorRef
import pl.edu.agh.flows.Sink
import pl.edu.agh.workflow_patterns.WorkflowProcess

trait MergeProcess[T, R] extends WorkflowProcess { actor: MergeActor[T, R] =>
  protected var _outs = {
    var outsSeq = Seq.empty[ActorRef]
    for (i <- 0 until actor.numOfOuts) {
      outsSeq :+= Sink[R]("out" + i, actor.context)
    }
    outsSeq
  }
  def outs = _outs
}
