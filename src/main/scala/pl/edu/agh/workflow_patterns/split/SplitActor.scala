package pl.edu.agh.workflow_patterns.split

import akka.actor.{ActorLogging, Props}
import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.messages._
import pl.edu.agh.workflow_patterns.{PatternOuts, PatternActor}

class SplitActor[T, R](numOfOuts: Int, outs: Seq[String], var action: ISingleAction[T, R]) extends PatternActor(numOfOuts, outs, action) with PatternOuts[R] with ActorLogging {
  def receive = {
    case DataMessage(data: T) =>
      val res = action.execute(data)
      _outs foreach (_ ! ResultMessage(res))
      userDefinedOuts.values foreach (_ ! ResultMessage(res))
    case ChangeAction(act: ISingleAction[T, R]) =>
      action = act
    case Get =>
      sender ! this
  }
}

object SplitActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, R](name: String, numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R]) = system.actorOf(SplitActor.props(numOfOuts, outs, action), name)
  def props[T, R](numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R]) = Props(classOf[SplitActor[T, R]], numOfOuts, outs, action)
}
