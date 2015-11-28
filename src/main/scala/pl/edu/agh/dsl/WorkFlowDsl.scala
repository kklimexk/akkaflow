package pl.edu.agh.dsl

import akka.actor.ActorRef
import pl.edu.agh.flows.Source
import pl.edu.agh.messages.DataMessage

object WorkFlowDsl {
  /*implicit class TwoInChannels(channels: (InChannel, InChannel)) {
    def ~>(actor: ActorRef) = {
      channels._1 ~> actor
      channels._2 ~> actor
      actor
    }
  }*/
  /*implicit class ActorToActor(flow: ActorRef) {
    def ~>(flowNext: ActorRef) = {
      flow ! Dest(flowNext)
      flowNext
    }
  }*/
  implicit class InputData(source: Source) {
    def ~>(flow: ActorRef) = {
      source.data.foreach { d =>
        flow ! DataMessage(d)
      }
      flow
    }
  }
  implicit class ForwardIteratorDataToNext(data: Iterator[List[Int]]) {
    def ~>(flow: ActorRef) = {
      data.toList.foreach { d =>
        flow ! DataMessage(d)
      }
      flow
    }
  }
  implicit class ForwardListDataToNext(data: List[List[Int]]) {
    def ~>(flow: ActorRef) = {
      data.foreach { d =>
        flow ! DataMessage(d)
      }
      flow
    }
  }
}
