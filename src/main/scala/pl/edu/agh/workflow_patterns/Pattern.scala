package pl.edu.agh.workflow_patterns

import akka.actor.ActorRef
import pl.edu.agh.actions.ActionConverter
import pl.edu.agh.messages.{ChangeAction, ChangeSendTo}

trait IPattern

trait Pattern[T, R] extends IPattern {
  val actor: ActorRef

  def changeActionOn(action: T => R) = actor ! ChangeAction(ActionConverter(action))
  def sendTo(outName: String) = actor ! ChangeSendTo(outName)
}
