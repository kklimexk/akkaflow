package pl.edu.agh.workflow_patterns.choice

import akka.actor.ActorRef
import pl.edu.agh.workflow_patterns.WorkflowProcess

trait ChoiceProcess[T, R] extends WorkflowProcess {
  protected var _outs: Seq[ActorRef]
  def outs: Seq[ActorRef]
}
