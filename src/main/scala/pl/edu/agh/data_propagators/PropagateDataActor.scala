package pl.edu.agh.data_propagators

import akka.actor.{Actor, Props}
import pl.edu.agh.messages.{DataMessage, PropagateData}

class PropagateDataActor[T](data: T) extends Actor {
  def receive = {
    case PropagateData(elem) =>
      data match {
        case res: TraversableOnce[T] =>
          res.foreach { d =>
            elem.actor ! DataMessage(d)
          }
        case _ => throw new Exception("This type is not subclass of TraversableOnce!")
      }
  }
}

object PropagateDataActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T](data: T) = system.actorOf(PropagateDataActor.props(data))
  def props[T](data: T) = Props(classOf[PropagateDataActor[T]], data)
}
