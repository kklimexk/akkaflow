package pl.edu.agh.dsl

import pl.edu.agh.actions._
import pl.edu.agh.flows.{In, Out, Source}
import pl.edu.agh.messages._
import pl.edu.agh.workflow_patterns.choice.Choice
import pl.edu.agh.workflow_patterns.merge.{PropagateDataForMergeActor, Merge}
import pl.edu.agh.workflow_patterns.synchronization.{PropagateDataForMultipleSyncActor, MultipleSync, Sync}

object WorkFlowDsl {

  implicit class InputDataToWorkflow(source: Source) {
    /*def ~>(workflow: Workflow) = {
      source.data.foreach { d =>
        workflow.in.data :+= d
      }
    }*/
    def ~>(in: In) = {
      source.data.foreach { d =>
        in.data :+= d
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
    def ~>>[T](elem: Choice[T]) = {
      in.data.foreach { d =>
        elem.choiceActor ! DataMessage(d)
      }
      elem
    }
    def ~>>[T](elem: Merge[T]) = {
      in.data.foreach { d =>
        elem.mergeActor ! DataMessage(d)
      }
      elem
    }
  }

  implicit class TwoInputsDataToNext(ins: (In, In)) {
    def ~>>[T](elem: MultipleSync[T]) = {
      PropagateDataForMultipleSyncActor(ins._1.data) ! PropagateDataForMultipleSync(elem, 0)
      PropagateDataForMultipleSyncActor(ins._2.data) ! PropagateDataForMultipleSync(elem, 1)
    }
  }

  implicit class ThreeInputsDataToNext(ins: (In, In, In)) {
    def ~>>[T](elem: MultipleSync[T]) = {
      PropagateDataForMultipleSyncActor(ins._1.data) ! PropagateDataForMultipleSync(elem, 0)
      PropagateDataForMultipleSyncActor(ins._2.data) ! PropagateDataForMultipleSync(elem, 1)
      PropagateDataForMultipleSyncActor(ins._2.data) ! PropagateDataForMultipleSync(elem, 2)
    }
  }

  implicit class FourInputsDataToNext(ins: (In, In, In, In)) {
    def ~>>[T](elem: MultipleSync[T]) = {
      PropagateDataForMultipleSyncActor(ins._1.data) ! PropagateDataForMultipleSync(elem, 0)
      PropagateDataForMultipleSyncActor(ins._2.data) ! PropagateDataForMultipleSync(elem, 1)
      PropagateDataForMultipleSyncActor(ins._2.data) ! PropagateDataForMultipleSync(elem, 2)
      PropagateDataForMultipleSyncActor(ins._2.data) ! PropagateDataForMultipleSync(elem, 3)
    }
  }

  implicit class ResultToNext(data: List[Int]) {
    def ~>[T](elem: Merge[T]) = {
      PropagateDataForMergeActor(data) ! PropagateDataForMerge(elem)
    }
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
    def ->[T](action: ISingleAction[T]) = {
      action
    }
    def ->[T](action: IMultipleAction[T]) = {
      action
    }
  }

  def send[T](action: ISingleAction[T]) = action
  def send[T](action: IMultipleAction[T]) = action

}
