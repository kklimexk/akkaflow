package pl.edu.agh.workflow_patterns.synchronization

import akka.actor.{Actor, Props}
import pl.edu.agh.messages._

class PropagateDataForMultipleSyncActor[T](data: List[T]) extends Actor {
  def receive = {
    case PropagateDataForMultipleSync(elem, uId) =>
      data.foreach { d =>
        elem.syncActor ! SyncDataMessage(d, uId)
      }
  }
}

object PropagateDataForMultipleSyncActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T](data: List[T]) = system.actorOf(PropagateDataForMultipleSyncActor.props(data))
  def props[T](data: List[T]) = Props(classOf[PropagateDataForMultipleSyncActor[T]], data)
}
