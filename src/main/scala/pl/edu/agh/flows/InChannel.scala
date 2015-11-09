package pl.edu.agh.flows

import akka.actor.ActorRef
import pl.edu.agh.messages.DMessage

class InChannel(message: DMessage) {
  def ~>(actor: ActorRef) = {
    actor ! message
  }
}

object InChannel {
  def apply(message: DMessage) = new InChannel(message)
}
