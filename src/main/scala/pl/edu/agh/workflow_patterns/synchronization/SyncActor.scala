package pl.edu.agh.workflow_patterns.synchronization

import java.util.concurrent.ConcurrentLinkedQueue

import pl.edu.agh.workflow_patterns.{PatternOuts, PatternActor}

import scala.util.control.Breaks._

import akka.actor.{Props, ActorLogging}
import pl.edu.agh.actions.IMultipleAction
import pl.edu.agh.messages._

class SyncActor[T, R](numOfOuts: Int, outs: Seq[String], var multipleAction: IMultipleAction[T, R], var sendTo: String, syncPoints: Seq[ConcurrentLinkedQueue[T]]) extends PatternActor(numOfOuts, outs, multipleAction) with PatternOuts[R] with ActorLogging {
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
    case ChangeAction(act: IMultipleAction[T, R]) =>
      multipleAction = act
    case ChangeSendTo(outName) =>
      sendTo = outName
    case Get =>
      sender ! this
  }

}

object SyncActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, R](name: String, numOfOuts: Int, outs: Seq[String], action: IMultipleAction[T, R], sendTo: String, syncPoints: Seq[ConcurrentLinkedQueue[T]]) = system.actorOf(SyncActor.props(numOfOuts, outs, action, sendTo, syncPoints), name)
  def props[T, R](numOfOuts: Int, outs: Seq[String], action: IMultipleAction[T, R], sendTo: String, syncPoints: Seq[ConcurrentLinkedQueue[T]]) = Props(classOf[SyncActor[T, R]], numOfOuts, outs, action, sendTo, syncPoints)
}
