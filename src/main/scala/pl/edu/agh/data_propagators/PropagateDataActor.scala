package pl.edu.agh.data_propagators

import akka.actor.{Actor, Props}
import pl.edu.agh.messages.{DataMessage, PropagateData}

class PropagateDataActor[T](data: List[T]) extends Actor {

  def receive = {
    case PropagateData(elem) =>
      data.foreach { d =>
        elem.actor ! DataMessage(d)
      }
  }
}

object PropagateDataActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T](data: List[T]) = system.actorOf(PropagateDataActor.props(data))
  def props[T](data: List[T]) = Props(classOf[PropagateDataActor[T]], data)
}
