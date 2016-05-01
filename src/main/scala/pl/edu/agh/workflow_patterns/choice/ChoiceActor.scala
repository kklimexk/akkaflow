package pl.edu.agh.workflow_patterns.choice

import akka.actor.{ActorLogging, Props}
import pl.edu.agh.actions.{ISingleAction, Outs}
import pl.edu.agh.messages.{ChangeAction, DataMessage, Get}
import pl.edu.agh.workflow_patterns.{PatternActor, PatternOuts}

//Choice Pattern
@deprecated(message = "There is no need to use it!")
class ChoiceActor[T, R](numOfOuts: Int, outs: Seq[String], var action: ISingleAction[T, R]) extends PatternActor(numOfOuts, outs, action) with PatternOuts[R] with ActorLogging {
  def receive = {
    case DataMessage(data: T) =>
      if (numOfOuts > 0) {
        action.execute(data)(Outs(_outs))
      }
      if (outs.nonEmpty) {
        action.execute(data)(Outs(userDefinedOuts))
      }
    case ChangeAction(act: ISingleAction[T, R]) =>
      action = act
    case Get =>
      sender ! this
  }
}

@deprecated(message = "There is no need to use it!")
object ChoiceActor {
  import pl.edu.agh.utils.ActorUtils.system

  @deprecated(message = "There is no need to use it!")
  def apply[T, R](name: String, numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R]) = system.actorOf(ChoiceActor.props(numOfOuts, outs, action), name)
  def props[T, R](numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R]) = Props(classOf[ChoiceActor[T, R]], numOfOuts, outs, action)
}
