package pl.edu.agh.workflow_patterns.choice

import akka.actor.{ActorLogging, Actor, Props}
import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.messages.{Get, DataMessage}

//Choice Pattern
class ChoiceActor[T, K](action: ISingleAction[T, K], conditions: K => (Boolean, Boolean, Boolean)) extends Actor with ChoiceProcess[K] with ActorLogging {
  def receive = {
    case DataMessage(data: T) =>
      var res = action.execute(data)
      //log.info("Computing action: {}", res)
      val conds = conditions(res)
      res match {
        case x if conds._1 => out1 :+= res
        case x if conds._2 => out2 :+= res
        case x if conds._3 => out3 :+= res
      }
    case Get =>
      sender ! this
  }
}

object ChoiceActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, K](action: ISingleAction[T, K], conditions: K => (Boolean, Boolean, Boolean)) = system.actorOf(ChoiceActor.props(action, conditions))
  def apply[T, K](name: String, action: ISingleAction[T, K], conditions: K => (Boolean, Boolean, Boolean)) = system.actorOf(ChoiceActor.props(action, conditions), name)
  def props[T, K](action: ISingleAction[T, K], conditions: K => (Boolean, Boolean, Boolean)) = Props(classOf[ChoiceActor[T, K]], action, conditions)
}
