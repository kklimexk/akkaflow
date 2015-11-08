package pl.edu.agh.workflow_patterns.synchronization

import akka.actor.{ActorLogging, ActorSystem, Props, Actor}
import pl.edu.agh.flows.OutChannelHandler
import pl.edu.agh.messages._

class SynchronizationPattern extends Actor with OutChannelHandler with ActorLogging {

  //val messagesQueue = mutable.Queue.empty[DataMessage]
  var sum = 0

  def receive = {
    case IntDataMessage(num) =>
      sum += num
    case Result =>
      log.info("Sending result: {}", sum)
      out ! IntResultMessage(sum)
  }
}

object SynchronizationPattern {
  def apply(system: ActorSystem, name: String) = system.actorOf(SynchronizationPattern.props, name)
  def props = Props[SynchronizationPattern]
}
