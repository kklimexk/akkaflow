package pl.edu.agh.workflow_patterns.choice

import akka.actor.{ActorLogging, Actor, Props}
import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.messages.{ResultMessage, Get, DataMessage}

//Choice Pattern
class ChoiceActor[T, K](numOfOuts: Int, action: ISingleAction[T, K], choiceFunc: K => Int) extends ChoiceProcess[T, K](numOfOuts) with Actor with ActorLogging {
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

  def apply[T, K](numOfOuts: Int, action: ISingleAction[T, K], choiceFunc: K => Int) = system.actorOf(ChoiceActor.props(numOfOuts, action, choiceFunc))
  def apply[T, K](name: String, numOfOuts: Int, action: ISingleAction[T, K], choiceFunc: K => Int) = system.actorOf(ChoiceActor.props(numOfOuts, action, choiceFunc), name)
  def props[T, K](numOfOuts: Int, action: ISingleAction[T, K], choiceFunc: K => Int) = Props(classOf[ChoiceActor[T, K]], numOfOuts, action, choiceFunc)
}
