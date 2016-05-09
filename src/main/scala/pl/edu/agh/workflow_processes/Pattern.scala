package pl.edu.agh.workflow_processes

import akka.actor.ActorRef
import pl.edu.agh.actions.{ActionConverter, Ins, Outs}
import pl.edu.agh.messages.ChangeAction

trait IPattern

trait Pattern[T, R] extends IPattern {
  val actor: ActorRef

  def changeActionOn(action: (T, Outs) => Unit) = actor ! ChangeAction(ActionConverter[T, R](action))
  def changeActionOn(action: (Ins[T], Outs) => Unit)(implicit d: DummyImplicit) = actor ! ChangeAction(ActionConverter[T, R](action))

  //def sendTo(outName: String) = actor ! ChangeSendTo(outName)
}
