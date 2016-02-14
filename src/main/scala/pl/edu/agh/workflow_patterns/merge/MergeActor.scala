package pl.edu.agh.workflow_patterns.merge

import akka.actor.{Props, ActorLogging, Actor}
import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.messages.{DataMessage, Get}

class MergeActor[T, K](action: ISingleAction[T, K]) extends Actor with MergeProcess[K] with ActorLogging {
  def receive = {
    case DataMessage(data: T) =>
      //log.info("DATA: {}", data)
      var res = action.execute(data)
      _out :+= res
    case Get =>
      sender ! this
  }
}

object MergeActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, K](action: ISingleAction[T, K]) = system.actorOf(MergeActor.props(action))
  def apply[T, K](name: String, action: ISingleAction[T, K]) = system.actorOf(MergeActor.props(action), name)
  def props[T, K](action: ISingleAction[T, K]) = Props(classOf[MergeActor[T, K]], action)
}
