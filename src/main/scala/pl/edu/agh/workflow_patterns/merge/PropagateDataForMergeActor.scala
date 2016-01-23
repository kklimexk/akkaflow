package pl.edu.agh.workflow_patterns.merge

import akka.actor.{Actor, Props}
import pl.edu.agh.messages.{DataMessage, PropagateDataForMerge}

class PropagateDataForMergeActor(data: List[Int]) extends Actor {

  def receive = {
    case PropagateDataForMerge(elem) =>
      data.foreach { d =>
        elem.mergeActor ! DataMessage(d)
      }
  }
}

object PropagateDataForMergeActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply(data: List[Int]) = system.actorOf(PropagateDataForMergeActor.props(data))
  def props(data: List[Int]) = Props(classOf[PropagateDataForMergeActor], data)
}
