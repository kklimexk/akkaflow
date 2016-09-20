package pl.edu.agh.workflow_processes.simple

import akka.actor.Props
import com.typesafe.config.ConfigFactory
import pl.edu.agh.actions.{ISingleAction, Outs}
import pl.edu.agh.messages._
import pl.edu.agh.utils.FSMStates._
import pl.edu.agh.workflow_processes.{PatternActor, PatternOuts}

import scala.concurrent.duration._

class ProcessActor[T, R](numOfOuts: Int, outs: Seq[String], var _action: ISingleAction[T, R]) extends PatternActor(numOfOuts, outs, _action) with PatternOuts[R] {

  lazy val time = ConfigFactory.load().getInt("config.state.stateTimeout")

  startWith(Init, Uninitialized)

  when(Init) {
    case Event(DataMessage(data: T), _) =>
      _action.execute(data)(Outs(_outs))
      goto(Active)
    case Event(ChangeAction(act: ISingleAction[T, R]), _) =>
      _action = act
      goto(Active)
  }

  when(Active) {
    case Event(DataMessage(data: T), _) =>
      _action.execute(data)(Outs(_outs))
      stay
    case Event(ChangeAction(act: ISingleAction[T, R]), _) =>
      _action = act
      stay
    case Event(Flush | StateTimeout, _) =>
      goto(Idle)
  }

  when(Idle) {
    case Event(DataMessage(data: T), _) =>
      _action.execute(data)(Outs(_outs))
      goto(Active)
    case Event(ChangeAction(act: ISingleAction[T, R]), _) =>
      _action = act
      goto(Active)
  }

  onTransition {
    case _ -> Active => setTimer("stateTimeout", Flush, time.seconds)
  }

  whenUnhandled {
    case Event(Get, _) =>
      sender ! this
      stay
  }

  initialize()
}

object ProcessActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, R](name: String, numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R]) = system.actorOf(ProcessActor.props(numOfOuts, outs, action), name)
  def props[T, R](numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R]) = Props(classOf[ProcessActor[T, R]], numOfOuts, outs, action)
}
