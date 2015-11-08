package pl.edu.agh.flows

import akka.actor.ActorRef
import pl.edu.agh.messages.DataMessage

class InChannel(message: DataMessage) {
  def ~>(actor: ActorRef) = {
    actor ! message
  }
}

object InChannel {
  def apply(message: DataMessage) = new InChannel(message)
}
