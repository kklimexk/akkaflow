package pl.edu.agh.workflow_processes.simple

import akka.actor.{ActorLogging, Props}
import pl.edu.agh.actions.{ISingleAction, Outs}
import pl.edu.agh.messages._
import pl.edu.agh.workflow_processes.{PatternActor, PatternOuts}

class ProcessActor[T, R](numOfOuts: Int, outs: Seq[String], var _action: ISingleAction[T, R]) extends PatternActor(numOfOuts, outs, _action) with PatternOuts[R] with ActorLogging {
  def receive = {
    case DataMessage(data: T) =>
      _action.execute(data)(Outs(_outs))
    case ChangeAction(act: ISingleAction[T, R]) =>
      _action = act
    /*case ChangeSendTo(outName) =>
      sendTo = outName*/
    case Get =>
      sender ! this
  }
}

object ProcessActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, R](name: String, numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R]) = system.actorOf(ProcessActor.props(numOfOuts, outs, action), name)
  def props[T, R](numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R]) = Props(classOf[ProcessActor[T, R]], numOfOuts, outs, action)
}
