package pl.edu.agh.workflow_patterns

import akka.actor.ActorRef

trait WorkflowProcess {
  protected var _outs: Seq[ActorRef]
  def outs: Seq[ActorRef]
}
