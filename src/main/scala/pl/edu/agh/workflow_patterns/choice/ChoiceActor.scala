package pl.edu.agh.workflow_patterns.choice

import akka.actor.{ActorLogging, Actor, Props}
import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.messages.{Get, DataMessage}

//Choice Pattern
class ChoiceActor[T, K](numOfIns: Int, numOfOuts: Int, action: ISingleAction[T, K], choiceFunc: K => Int) extends ChoiceProcess[T, K](numOfIns, numOfOuts) with Actor with ActorLogging {
  def receive = {
    case DataMessage(data: T) =>
      val res = action.execute(data)
      //log.info("Computing action: {}", res)
      val i = choiceFunc(res) % outs.length

      outs(i) += res
    case Get =>
      sender ! this
  }
}

object ChoiceActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, K](numOfIns: Int, numOfOuts: Int, action: ISingleAction[T, K], choiceFunc: K => Int) = system.actorOf(ChoiceActor.props(numOfIns, numOfOuts, action, choiceFunc))
  def apply[T, K](name: String, numOfIns: Int, numOfOuts: Int, action: ISingleAction[T, K], choiceFunc: K => Int) = system.actorOf(ChoiceActor.props(numOfIns, numOfOuts, action, choiceFunc), name)
  def props[T, K](numOfIns: Int, numOfOuts: Int, action: ISingleAction[T, K], choiceFunc: K => Int) = Props(classOf[ChoiceActor[T, K]], numOfIns, numOfOuts, action, choiceFunc)
}
