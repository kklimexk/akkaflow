package pl.edu.agh.workflow_patterns.synchronization

import akka.actor.{Props, ActorLogging, Actor}
import pl.edu.agh.actions.IMultipleAction
import pl.edu.agh.messages.{Get, DataMessage}
import pl.edu.agh.workflow_patterns.WorkflowProcess

class MultipleSyncActor[T](multipleAction: IMultipleAction[T]) extends Actor with WorkflowProcess with ActorLogging {

  var syncPoint = Seq.empty[T]

  def receive = {
    case DataMessage(data: T) =>
      syncPoint :+= data

      if (syncPoint.length == multipleAction.numOfIns) {
        res = multipleAction.execute(syncPoint:_*)
        //log.info("Computing action: {}", res)
        out :+= res
        syncPoint = Seq.empty[T]
      }
    case Get =>
      sender ! this
  }
}

object MultipleSyncActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T](action: IMultipleAction[T]) = system.actorOf(MultipleSyncActor.props(action))
  def apply[T](name: String, action: IMultipleAction[T]) = system.actorOf(MultipleSyncActor.props(action), name)
  def props[T](action: IMultipleAction[T]) = Props(classOf[MultipleSyncActor[T]], action)
}
