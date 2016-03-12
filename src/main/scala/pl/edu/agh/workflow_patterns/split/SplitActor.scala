package pl.edu.agh.workflow_patterns.split

import akka.actor.{ActorLogging, Props}
import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.messages.{ResultMessage, DataMessage, Get}
import pl.edu.agh.workflow_patterns.{PatternOuts, PatternActor}

class SplitActor[T, R](numOfOuts: Int, action: ISingleAction[T, R]) extends PatternActor(numOfOuts, action) with PatternOuts[R] with ActorLogging {
  def receive = {
    case DataMessage(data: T) =>
      val res = action.execute(data)
      outs foreach (_ ! ResultMessage(res))
    case Get =>
      sender ! this
  }
}

object SplitActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, R](name: String, numOfOuts: Int, action: ISingleAction[T, R]) = system.actorOf(SplitActor.props(numOfOuts, action), name)
  def props[T, R](numOfOuts: Int, action: ISingleAction[T, R]) = Props(classOf[SplitActor[T, R]], numOfOuts, action)
}
