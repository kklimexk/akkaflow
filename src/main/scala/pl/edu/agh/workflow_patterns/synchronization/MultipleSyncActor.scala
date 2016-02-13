package pl.edu.agh.workflow_patterns.synchronization

import java.util.concurrent.ConcurrentLinkedQueue
import scala.util.control.Breaks._

import akka.actor.{Props, ActorLogging, Actor}
import pl.edu.agh.actions.IMultipleAction
import pl.edu.agh.messages._

class MultipleSyncActor[T, K](multipleAction: IMultipleAction[T, K], syncPoints: Seq[ConcurrentLinkedQueue[T]]) extends Actor with SyncProcess[K] with ActorLogging {

  def receive = {
    case SyncDataMessage(data: T, uId) =>
      syncPoints(uId).offer(data)
      self ! GetResult

    case GetResult =>

      var canExecuteAction = true

      breakable {
        for (q <- syncPoints) {
          val el = q.peek()
          if (el == null) {
            canExecuteAction = false
            break
          }
        }
      }

      if (canExecuteAction) {
        var sync = Seq.empty[T]
        syncPoints.foreach { q =>
          sync :+= q.poll()
        }
        var res = multipleAction.execute(sync:_*)
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

  def apply[T, K](action: IMultipleAction[T, K], syncPoints: Seq[ConcurrentLinkedQueue[T]]) = system.actorOf(MultipleSyncActor.props(action, syncPoints))
  def props[T, K](action: IMultipleAction[T, K], syncPoints: Seq[ConcurrentLinkedQueue[T]]) = Props(classOf[MultipleSyncActor[T, K]], action, syncPoints)
}
