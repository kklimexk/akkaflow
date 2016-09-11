package pl.edu.agh.workflow_processes.discriminator

import java.util.concurrent.ConcurrentLinkedQueue

import akka.actor.{ActorLogging, Props}
import pl.edu.agh.actions.{IMultipleAction, Ins, Outs}
import pl.edu.agh.messages._
import pl.edu.agh.workflow_processes.{PatternActor, PatternOuts}

class DiscActor[T, R](numOfOuts: Int, ins: Seq[String], n: Int, outs: Seq[String], var multipleAction: IMultipleAction[T, R], syncPoints: Seq[ConcurrentLinkedQueue[T]]) extends PatternActor(numOfOuts, outs, multipleAction) with PatternOuts[R] with ActorLogging {

  var synchronizedInputs = Set.empty[Int]

  def receive = {
    case SyncDataMessage(data: T, uId) =>
      syncPoints(uId).offer(data)
      self ! GetResult

    case GetResult =>

      for (i <- 0 until syncPoints.size) {
        val el = syncPoints(i).peek()
        if (el != null && synchronizedInputs.size < n) {
          synchronizedInputs += i
        }
      }

      if (synchronizedInputs.size == n) {
        var sync = Map.empty[String, T]
        if (ins.nonEmpty) {
          for (i <- synchronizedInputs) {
            sync = sync + (ins(i) -> syncPoints(i).poll())
          }
        } else {
          for (i <- synchronizedInputs) {
            sync = sync + (("in" + i) -> syncPoints(i).poll())
          }
        }
        synchronizedInputs = Set.empty[Int]
        multipleAction.execute(Ins(sync))(Outs(_outs))
      }
    case ChangeAction(act: IMultipleAction[T, R]) =>
      multipleAction = act
    /*case ChangeSendTo(outName) =>
      sendTo = outName*/
    case Get =>
      sender ! this
  }

}

object DiscActor {

  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, R](name: String, numOfOuts: Int, ins: Seq[String], n: Int, outs: Seq[String], action: IMultipleAction[T, R], syncPoints: Seq[ConcurrentLinkedQueue[T]]) = system.actorOf(DiscActor.props(numOfOuts, ins, n, outs, action, syncPoints), name)

  def props[T, R](numOfOuts: Int, ins: Seq[String], n: Int, outs: Seq[String], action: IMultipleAction[T, R], syncPoints: Seq[ConcurrentLinkedQueue[T]]) = Props(classOf[DiscActor[T, R]], numOfOuts, ins, n, outs, action, syncPoints)
}
