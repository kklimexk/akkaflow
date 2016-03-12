package pl.edu.agh.workflow_patterns.choice

import akka.actor.{ActorLogging, Actor, Props}
import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.messages.{ResultMessage, Get, DataMessage}

//Choice Pattern
class ChoiceActor[T, R](val numOfOuts: Int, action: ISingleAction[T, R], choiceFunc: R => Int) extends Actor with ChoiceProcess[T, R] with ActorLogging {
  def receive = {
    case DataMessage(data: T) =>
      val res = action.execute(data)
      //log.info("Computing action: {}", res)
      val i = choiceFunc(res) % _outs.length

      _outs(i) ! ResultMessage(res)
    case Get =>
      sender ! this
  }
}

object ChoiceActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, R](name: String, numOfOuts: Int, action: ISingleAction[T, R], choiceFunc: R => Int) = system.actorOf(ChoiceActor.props(numOfOuts, action, choiceFunc), name)
  def props[T, R](numOfOuts: Int, action: ISingleAction[T, R], choiceFunc: R => Int) = Props(classOf[ChoiceActor[T, R]], numOfOuts, action, choiceFunc)
}
