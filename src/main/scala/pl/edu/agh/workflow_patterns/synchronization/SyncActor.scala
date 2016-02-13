package pl.edu.agh.workflow_patterns.synchronization

import akka.actor.{ActorLogging, Props, Actor}
import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.messages._

//Synchronization Pattern
class SyncActor[T, K](action: ISingleAction[T, K]) extends Actor with SyncProcess[K] with ActorLogging {

  def receive = {
    case DataMessage(data: T) =>
      var res = action.execute(data)
      //log.info("Computing action: {}", res)
      _out :+= res
    case Get =>
      sender ! this
  }
}

object SyncActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, K](action: ISingleAction[T, K]) = system.actorOf(SyncActor.props(action))
  def apply[T, K](name: String, action: ISingleAction[T, K]) = system.actorOf(SyncActor.props(action), name)
  def props[T, K](action: ISingleAction[T, K]) = Props(classOf[SyncActor[T, K]], action)
}
