package pl.edu.agh.workflow_patterns.choice

import akka.actor.{ActorLogging, Props}
import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.messages.{ChangeAction, ResultMessage, Get, DataMessage}
import pl.edu.agh.workflow_patterns.{PatternOuts, PatternActor}

//Choice Pattern
class ChoiceActor[T, R](numOfOuts: Int, outs: Seq[String], var action: ISingleAction[T, R], choiceFunc: R => Int) extends PatternActor(numOfOuts, outs, action) with PatternOuts[R] with ActorLogging {
  def receive = {
    case DataMessage(data: T) =>
      val res = action.execute(data)
      //log.info("Computing action: {}", res)
      if (numOfOuts > 0) {
        val i = choiceFunc(res) % _outs.length
        _outs(i) ! ResultMessage(res)
      }
      if (outs.nonEmpty) {
        val i = choiceFunc(res) % outs.length
        userDefinedOuts.values.toSeq(i) ! ResultMessage(res)
      }
    case ChangeAction(act: ISingleAction[T, R]) =>
      action = act
    case Get =>
      sender ! this
  }
}

object ChoiceActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, R](name: String, numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R], choiceFunc: R => Int) = system.actorOf(ChoiceActor.props(numOfOuts, outs, action, choiceFunc), name)
  def props[T, R](numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R], choiceFunc: R => Int) = Props(classOf[ChoiceActor[T, R]], numOfOuts, outs, action, choiceFunc)
}
