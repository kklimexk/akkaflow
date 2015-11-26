package pl.edu.agh.dsl

import akka.actor.ActorRef
import pl.edu.agh.messages.{DataMessage, Dest}

object WorkFlowDsl {
  /*implicit class TwoInChannels(channels: (InChannel, InChannel)) {
    def ~>(actor: ActorRef) = {
      channels._1 ~> actor
      channels._2 ~> actor
      actor
    }
  }*/
  implicit class ActorToActor(flow: ActorRef) {
    def ~>(flowNext: ActorRef) = {
      flow ! Dest(flowNext)
      flowNext
    }
  }
  implicit class InputData(data: Range) {
    def ~>(flow: ActorRef) = {
      data.foreach { d =>
        flow ! DataMessage(d)
      }
      flow
    }
  }
}
