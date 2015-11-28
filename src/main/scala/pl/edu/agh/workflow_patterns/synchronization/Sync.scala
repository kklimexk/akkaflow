package pl.edu.agh.workflow_patterns.synchronization

import akka.actor.{ActorLogging, ActorSystem, Props, Actor}
import pl.edu.agh.actions.Action
import pl.edu.agh.messages._

//Synchronization Pattern
class Sync[T](action: Action[T]) extends Actor with ActorLogging {

  var res: Int = _
  var out = List.empty[Int]

  def receive = {
    case DataMessage(data: T) =>
      res = action.execute(data)
      log.info("Computing action: {}", res)
      out :+= res
    case Get =>
      sender ! this
  }
}

object Sync {
  def apply[T](name: String, action: Action[T])(implicit system: ActorSystem) = system.actorOf(Sync.props(action), name)
  def props[T](action: Action[T]) = Props(classOf[Sync[T]], action)
}
