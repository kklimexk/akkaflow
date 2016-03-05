package pl.edu.agh.workflow_patterns.synchronization

import akka.actor.ActorRef
import pl.edu.agh.workflow_patterns.WorkflowProcess

trait SyncProcess[R] extends WorkflowProcess {
  protected var _outs: Seq[ActorRef]
  def outs: Seq[ActorRef]
}
