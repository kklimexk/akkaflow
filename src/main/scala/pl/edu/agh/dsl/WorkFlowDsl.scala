package pl.edu.agh.dsl

import akka.actor.ActorRef
import pl.edu.agh.flows.InChannel
import pl.edu.agh.messages.Dest

object WorkFlowDsl {
  implicit class TwoInChannels(channels: (InChannel, InChannel)) {
    def ~>(actor: ActorRef) = {
      channels._1 ~> actor
      channels._2 ~> actor
      actor
    }
  }
  implicit class ActorSendResult(flow: ActorRef) {
    def ~>(out: ActorRef) = {
      flow ! Dest(out)
    }
  }
}
