package pl.edu.agh.data_propagators

import akka.actor.{Actor, Props}
import pl.edu.agh.messages._

class PropagateDataForMultipleSyncActor[T](data: T) extends Actor {
  def receive = {
    case PropagateDataForMultipleSync(elem, uId) =>
      data match {
        case res: TraversableOnce[T] =>
          res.foreach { d =>
            elem.actor ! SyncDataMessage(d, uId)
          }
        case _ => throw new Exception("This type is not subclass of TraversableOnce!")
      }
  }
}

object PropagateDataForMultipleSyncActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T](data: T) = system.actorOf(PropagateDataForMultipleSyncActor.props(data))
  def props[T](data: T) = Props(classOf[PropagateDataForMultipleSyncActor[T]], data)
}
