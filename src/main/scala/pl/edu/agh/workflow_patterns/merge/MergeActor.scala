package pl.edu.agh.workflow_patterns.merge

import akka.actor.{Props, ActorLogging, Actor}
import pl.edu.agh.messages.{DataMessage, Get}

class MergeActor[T] extends Actor with MergeProcess with ActorLogging {
  def receive = {
    case DataMessage(data: Int) =>
      //log.info("DATA: {}", data)
      _out :+= data
    case Get =>
      sender ! this
  }
}

object MergeActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T] = system.actorOf(MergeActor.props)
  def props[T] = Props[MergeActor[T]]
}
