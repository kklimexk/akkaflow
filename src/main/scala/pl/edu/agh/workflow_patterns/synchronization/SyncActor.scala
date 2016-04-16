package pl.edu.agh.workflow_patterns.synchronization

import java.util.concurrent.ConcurrentLinkedQueue

import pl.edu.agh.workflow_patterns.{PatternActor, PatternOuts}

import scala.util.control.Breaks._
import akka.actor.{ActorLogging, Props}
import pl.edu.agh.actions.{IMultipleAction, INamedMultipleAction, IUnnamedMultipleAction}
import pl.edu.agh.messages._

class SyncActor[T, R](numOfOuts: Int, ins: Seq[String], outs: Seq[String], var multipleAction: IMultipleAction[T, R], var sendTo: String, syncPoints: Seq[ConcurrentLinkedQueue[T]]) extends PatternActor(numOfOuts, outs, multipleAction) with PatternOuts[R] with ActorLogging {
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
        multipleAction match {
          case _: IUnnamedMultipleAction[T, R] =>
            var sync = Seq.empty[T]

            syncPoints.foreach { q =>
              sync :+= q.poll()
            }
            val res = multipleAction.execute(sync: _*)
            context.actorSelection(self.path + "/" + sendTo) ! ResultMessage(res)
          case _: INamedMultipleAction[T, R] =>
            var sync = Map.empty[String, T]

            for (i <- 0 until syncPoints.size) {
              sync = sync + (ins(i) -> syncPoints(i).poll())
            }
            val res = multipleAction.execute(sync)
            context.actorSelection(self.path + "/" + sendTo) ! ResultMessage(res)
        }
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

  def apply[T, R](name: String, numOfOuts: Int, ins: Seq[String], outs: Seq[String], action: IMultipleAction[T, R], sendTo: String, syncPoints: Seq[ConcurrentLinkedQueue[T]]) = system.actorOf(SyncActor.props(numOfOuts, ins, outs, action, sendTo, syncPoints), name)
  def props[T, R](numOfOuts: Int, ins: Seq[String], outs: Seq[String], action: IMultipleAction[T, R], sendTo: String, syncPoints: Seq[ConcurrentLinkedQueue[T]]) = Props(classOf[SyncActor[T, R]], numOfOuts, ins, outs, action, sendTo, syncPoints)
}
