package pl.edu.agh.workflow_patterns.synchronization

import akka.actor.{ActorLogging, Props, Actor}
import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.messages._
import pl.edu.agh.workflow_patterns.WorkflowProcess

//Synchronization Pattern
class SyncActor[T](action: ISingleAction[T]) extends Actor with WorkflowProcess with ActorLogging {

  def receive = {
    case DataMessage(data: T) =>
      res = action.execute(data)
      //log.info("Computing action: {}", res)
      out :+= res
    case Get =>
      sender ! this
  }
}

object SyncActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T](action: ISingleAction[T]) = system.actorOf(SyncActor.props(action))
  def apply[T](name: String, action: ISingleAction[T]) = system.actorOf(SyncActor.props(action), name)
  def props[T](action: ISingleAction[T]) = Props(classOf[SyncActor[T]], action)
}
