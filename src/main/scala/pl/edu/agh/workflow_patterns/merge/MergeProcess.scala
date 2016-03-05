package pl.edu.agh.workflow_patterns.merge

import akka.actor.ActorRef
import pl.edu.agh.workflow_patterns.WorkflowProcess

trait MergeProcess[R] extends WorkflowProcess {
  protected var _outs: Seq[ActorRef]
  def outs: Seq[ActorRef]
}
