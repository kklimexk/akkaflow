package pl.edu.agh.workflow_patterns.synchronization

import akka.actor.{ActorLogging, ActorSystem, Props, Actor}
import pl.edu.agh.messages._

//Synchronization Pattern
class Sync extends Actor with ActorLogging {

  //val messagesQueue = mutable.Queue.empty[DataMessage]
  var sum = 0

  def receive = {
    case DataMessage(data: Int) =>
      sum += data
    case Dest(out) =>
      log.info("Sending result: {}", sum)
      out ! ResultMessage(sum)
  }
}

object Sync {
  def apply(system: ActorSystem, name: String) = system.actorOf(Sync.props, name)
  def props = Props[Sync]
}
