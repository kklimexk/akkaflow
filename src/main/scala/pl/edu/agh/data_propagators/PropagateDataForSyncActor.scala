package pl.edu.agh.data_propagators

import akka.actor.{Actor, Props}
import pl.edu.agh.messages._

class PropagateDataForSyncActor[T](data: T) extends Actor {
  def receive = {
    case PropagateDataForSync(elem, uId) =>
      data match {
        case res: TraversableOnce[T] =>
          res.foreach { d =>
            elem.actor ! SyncDataMessage(d, uId)
          }
        case _ => throw new Exception("This type is not subclass of TraversableOnce!")
      }
    case PropagateDataForDisc(elem, uId) =>
      data match {
        case res: TraversableOnce[T] =>
          res.foreach { d =>
            elem.actor ! SyncDataMessage(d, uId)
          }
        case _ => throw new Exception("This type is not subclass of TraversableOnce!")
      }
  }
}

object PropagateDataForSyncActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T](data: T) = system.actorOf(PropagateDataForSyncActor.props(data))
  def props[T](data: T) = Props(classOf[PropagateDataForSyncActor[T]], data)
}
