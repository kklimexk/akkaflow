package pl.edu.agh.workflow_patterns.merge

import akka.actor.{ActorLogging, Props}
import pl.edu.agh.actions.{ISingleAction, Outs}
import pl.edu.agh.messages._
import pl.edu.agh.workflow_patterns.{PatternActor, PatternOuts}

class MergeActor[T, R](numOfOuts: Int, outs: Seq[String], var action: ISingleAction[T, R]) extends PatternActor(numOfOuts, outs, action) with PatternOuts[R] with ActorLogging {
  def receive = {
    case DataMessage(data: T) =>
        action.execute(data)(Outs(_outs))
    case ChangeAction(act: ISingleAction[T, R]) =>
      action = act
    /*case ChangeSendTo(outName) =>
      sendTo = outName*/
    case Get =>
      sender ! this
  }
}

object MergeActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, R](name: String, numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R]) = system.actorOf(MergeActor.props(numOfOuts, outs, action), name)
  def props[T, R](numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R]) = Props(classOf[MergeActor[T, R]], numOfOuts, outs, action)
}
