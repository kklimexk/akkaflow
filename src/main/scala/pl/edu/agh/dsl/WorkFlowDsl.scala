package pl.edu.agh.dsl

import akka.actor.ActorRef
import pl.edu.agh.actions.Action
import pl.edu.agh.flows.{In, Out, Source}
import pl.edu.agh.messages.DataMessage
import pl.edu.agh.workflow.Workflow

object WorkFlowDsl {

  implicit class InputData(source: Source) {
    def ~>(flow: ActorRef) = {
      source.data.foreach { d =>
        flow ! DataMessage(d)
      }
      flow
    }
    def ~>(workflow: Workflow) = {
      source.data.foreach { d =>
        workflow.in.data :+= d
      }
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

  implicit class ForwardInputDataToNext(in: In) {
    def ~>>(flow: ActorRef) = {
      in.data.foreach { d =>
        flow ! DataMessage(d)
      }
      flow
    }
  }

  implicit class ForwardResultToOutput(data: List[Int]) {
    def ~>>(out: Out) = {
      var outRes = out.result
      data.foreach { d =>
        outRes :+= d
      }
      out.result = outRes
      out
    }
  }

  implicit class ForwardListOfListsDataToNext(data: List[List[Int]]) {
    def ~>(flow: ActorRef) = {
      data.foreach { d =>
        flow ! DataMessage(d)
      }
      flow
    }
  }

  implicit object Send {
    def ->[T](action: Action[T]) = {
      action
    }
  }

  def send[T](action: Action[T]) = action

}
