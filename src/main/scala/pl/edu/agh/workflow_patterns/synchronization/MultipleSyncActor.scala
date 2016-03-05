package pl.edu.agh.workflow_patterns.synchronization

import java.util.concurrent.ConcurrentLinkedQueue
import pl.edu.agh.flows.Sink

import scala.util.control.Breaks._

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import pl.edu.agh.actions.IMultipleAction
import pl.edu.agh.messages._

class MultipleSyncActor[T, R](numOfOuts: Int, multipleAction: IMultipleAction[T, R], sendTo: String, syncPoints: Seq[ConcurrentLinkedQueue[T]]) extends Actor with SyncProcess[R] with ActorLogging {

  protected var _outs = {
    var outsSeq = Seq.empty[ActorRef]
    for (i <- 0 until numOfOuts) {
      outsSeq :+= Sink[R]("out" + i, context)
    }
    outsSeq
  }

  def outs = _outs

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
        val res = multipleAction.execute(sync:_*)
        context.actorSelection(self.path + "/" + sendTo) ! ResultMessage(res)
      }

    case Get =>
      sender ! this
  }

}

object MultipleSyncActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, R](name: String, numOfOuts: Int, action: IMultipleAction[T, R], sendTo: String, syncPoints: Seq[ConcurrentLinkedQueue[T]]) = system.actorOf(MultipleSyncActor.props(numOfOuts, action, sendTo, syncPoints), name)
  def props[T, R](numOfOuts: Int, action: IMultipleAction[T, R], sendTo: String, syncPoints: Seq[ConcurrentLinkedQueue[T]]) = Props(classOf[MultipleSyncActor[T, R]], numOfOuts, action, sendTo, syncPoints)
}
