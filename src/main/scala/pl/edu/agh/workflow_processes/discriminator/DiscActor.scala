package pl.edu.agh.workflow_processes.discriminator

import java.util.concurrent.ConcurrentLinkedQueue

import akka.actor.Props
import com.typesafe.config.ConfigFactory
import pl.edu.agh.actions.{IMultipleAction, Ins, Outs}
import pl.edu.agh.messages._
import pl.edu.agh.utils.FSMStates._
import pl.edu.agh.workflow_processes.{PatternActor, PatternOuts}

import scala.concurrent.duration._

class DiscActor[T, R](numOfOuts: Int, ins: Seq[String], n: Int, outs: Seq[String], var multipleAction: IMultipleAction[T, R], syncPoints: Seq[ConcurrentLinkedQueue[T]]) extends PatternActor(numOfOuts, outs, multipleAction) with PatternOuts[R] {

  lazy val time = ConfigFactory.load().getInt("config.state.stateTimeout")
  var synchronizedInputs = Set.empty[Int]

  startWith(Init, Uninitialized)

  when(Init) {
    case Event(SyncDataMessage(data: T, uId), _) =>
      syncPoints(uId).offer(data)
      self ! GetResult
      goto(Active)
    case Event(GetResult, _) =>
      executeGetResultEvent()
      goto(Active)
    case Event(ChangeAction(act: IMultipleAction[T, R]), _) =>
      multipleAction = act
      goto(Active)
  }

  when(Active, stateTimeout = time.seconds) {
    case Event(SyncDataMessage(data: T, uId), _) =>
      syncPoints(uId).offer(data)
      self ! GetResult
      stay forMax time.seconds
    case Event(GetResult, _) =>
      executeGetResultEvent()
      stay forMax time.seconds
    case Event(ChangeAction(act: IMultipleAction[T, R]), _) =>
      multipleAction = act
      stay forMax time.seconds
    case Event(Flush | StateTimeout, _) =>
      goto(Idle)
  }

  when(Idle) {
    case Event(SyncDataMessage(data: T, uId), _) =>
      syncPoints(uId).offer(data)
      self ! GetResult
      goto(Active)
    case Event(GetResult, _) =>
      executeGetResultEvent()
      goto(Active)
    case Event(ChangeAction(act: IMultipleAction[T, R]), _) =>
      multipleAction = act
      goto(Active)
  }

  whenUnhandled {
    case Event(Get, _) =>
      sender ! this
      stay
  }

  private def executeGetResultEvent() = {
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
  }

  initialize()
}

object DiscActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, R](name: String, numOfOuts: Int, ins: Seq[String], n: Int, outs: Seq[String], action: IMultipleAction[T, R], syncPoints: Seq[ConcurrentLinkedQueue[T]]) = system.actorOf(DiscActor.props(numOfOuts, ins, n, outs, action, syncPoints), name)
  def props[T, R](numOfOuts: Int, ins: Seq[String], n: Int, outs: Seq[String], action: IMultipleAction[T, R], syncPoints: Seq[ConcurrentLinkedQueue[T]]) = Props(classOf[DiscActor[T, R]], numOfOuts, ins, n, outs, action, syncPoints)
}
