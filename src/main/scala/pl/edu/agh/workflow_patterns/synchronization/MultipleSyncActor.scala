package pl.edu.agh.workflow_patterns.synchronization

import java.util.concurrent.ConcurrentLinkedQueue

import akka.actor.{Props, ActorLogging, Actor}
import pl.edu.agh.actions.IMultipleAction
import pl.edu.agh.messages._
import pl.edu.agh.workflow_patterns.WorkflowProcess

class MultipleSyncActor[T](multipleAction: IMultipleAction[T]) extends Actor with WorkflowProcess with ActorLogging {

  //Ewentualnie mozna uzyc LinkedBlockingQueue
  var syncPoint1 = new ConcurrentLinkedQueue[T]()
  var syncPoint2 = new ConcurrentLinkedQueue[T]()

  def receive = {
    case SyncDataMessage1(data: T) =>
      syncPoint1.offer(data)
      self ! GetResult

    case SyncDataMessage2(data: T) =>
      syncPoint2.offer(data)
      self ! GetResult

    case GetResult =>
      val el1 = syncPoint1.peek()
      val el2 = syncPoint2.peek()

      if (el1 != null && el2 != null) {
        res = multipleAction.execute(el1, el2)
        syncPoint1.poll()
        syncPoint2.poll()
        _out :+= res
      }

    case Get =>
      sender ! this
  }

  override def out = {
    //TODO: To trzeba zmienic
    Thread.sleep(200)
    _out
  }

}

object MultipleSyncActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T](action: IMultipleAction[T]) = system.actorOf(MultipleSyncActor.props(action))
  def apply[T](name: String, action: IMultipleAction[T]) = system.actorOf(MultipleSyncActor.props(action), name)
  def props[T](action: IMultipleAction[T]) = Props(classOf[MultipleSyncActor[T]], action)
}
