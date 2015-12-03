package pl.edu.agh.dsl

import pl.edu.agh.actions.Action
import pl.edu.agh.flows.{In, Out, Source}
import pl.edu.agh.messages.DataMessage
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.workflow_patterns.synchronization.Sync

object WorkFlowDsl {

  implicit class InputDataToWorkflow(source: Source) {
    def ~>(workflow: Workflow) = {
      source.data.foreach { d =>
        workflow.in.data :+= d
      }
    }
  }

  implicit class InputDataToNext(in: In) {
    def ~>>[T](elem: Sync[T]) = {
      in.data.foreach { d =>
        elem.syncActor ! DataMessage(d)
      }
      elem
    }
  }

  implicit class ResultToOutput(data: List[Int]) {
    def ~>>(out: Out) = {
      var outRes = out.result
      data.foreach { d =>
        outRes :+= d
      }
      out.result = outRes
      out
    }
  }

  implicit class ForwardIteratorDataToNext(data: Iterator[List[Int]]) {
    def ~>[T](elem: Sync[T]) = {
      data.toList.foreach { d =>
        elem.syncActor ! DataMessage(d)
      }
      elem
    }
  }

  implicit class ForwardListOfListsDataToNext(data: List[List[Int]]) {
    def ~>[T](elem: Sync[T]) = {
      data.foreach { d =>
        elem.syncActor ! DataMessage(d)
      }
      elem
    }
  }

  implicit object Send {
    def ->[T](action: Action[T]) = {
      action
    }
  }

  def send[T](action: Action[T]) = action

}
