package pl.edu.agh.workflow_patterns.synchronization

import akka.actor.ActorRef
import pl.edu.agh.flows.Sink
import pl.edu.agh.workflow_patterns.WorkflowProcess

trait SyncProcess[T, R] extends WorkflowProcess { actor: SyncActor[T, R] =>
  protected var _outs = {
    var outsSeq = Seq.empty[ActorRef]
    for (i <- 0 until actor.numOfOuts) {
      outsSeq :+= Sink[R]("out" + i, actor.context)
    }
    outsSeq
  }
  def outs = _outs
}
