package pl.edu.agh.workflow_patterns

import akka.actor.ActorRef
import pl.edu.agh.actions.IAction
import pl.edu.agh.messages.ChangeAction

trait IPattern

trait Pattern[T, R] extends IPattern {
  val actor: ActorRef
  def changeActionOn(action: IAction[T, R]) = {
    actor ! ChangeAction(action)
  }
}
