package pl.edu.agh.workflow_patterns.synchronization

import akka.actor.{ActorLogging, ActorSystem, Props, Actor}
import pl.edu.agh.messages._

//Synchronization Pattern
class Sync extends Actor with ActorLogging {

  //val messagesQueue = mutable.Queue.empty[DataMessage]
  var sqr = 1
  var out = List.empty[Int]

  def receive = {
    case DataMessage(data: Int) =>
      sqr = data * data
      log.info("Computing action: {}", sqr)
      out :+= sqr
    case Dest(flow) =>
      log.info("Sending result: {}", sqr)
      flow ! ResultMessage(sqr)
    case Get =>
      sender ! this
  }
}

object Sync {
  def apply(name: String)(implicit system: ActorSystem) = system.actorOf(Sync.props, name)
  def props = Props[Sync]
}
