package pl.edu.agh.workflow_patterns.synchronization

import akka.actor.{Actor, Props}
import pl.edu.agh.messages._

class PropagateDataForMultipleSyncActor(data: List[Int]) extends Actor {
  def receive = {
    case PropagateDataForMultipleSync(elem, uId) =>
      data.foreach { d =>
        elem.syncActor ! SyncDataMessage(d, uId)
      }
  }
}

object PropagateDataForMultipleSyncActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply(data: List[Int]) = system.actorOf(PropagateDataForMultipleSyncActor.props(data))
  def props(data: List[Int]) = Props(classOf[PropagateDataForMultipleSyncActor], data)
}
