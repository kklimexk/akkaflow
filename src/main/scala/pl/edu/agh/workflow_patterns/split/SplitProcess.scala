package pl.edu.agh.workflow_patterns.split

import akka.actor.ActorRef
import pl.edu.agh.workflow_patterns.WorkflowProcess

trait SplitProcess[R] extends WorkflowProcess {
  protected var _outs: Seq[ActorRef]
  def outs: Seq[ActorRef]
}
