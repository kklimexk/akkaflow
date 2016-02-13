package pl.edu.agh.workflow_patterns.merge

import akka.actor.{Actor, Props}
import pl.edu.agh.messages.{DataMessage, PropagateDataForMerge}

class PropagateDataForMergeActor[T](data: List[T]) extends Actor {

  def receive = {
    case PropagateDataForMerge(elem) =>
      data.foreach { d =>
        elem.mergeActor ! DataMessage(d)
      }
  }
}

object PropagateDataForMergeActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T](data: List[T]) = system.actorOf(PropagateDataForMergeActor.props(data))
  def props[T](data: List[T]) = Props(classOf[PropagateDataForMergeActor[T]], data)
}
