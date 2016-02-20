package pl.edu.agh.dsl

import akka.actor.ActorRef
import pl.edu.agh.data_propagators.{PropagateDataForMultipleSyncActor, PropagateDataActor}
import pl.edu.agh.flows._
import pl.edu.agh.messages._
import pl.edu.agh.utils.SinkUtils
import pl.edu.agh.workflow_patterns.Pattern
import pl.edu.agh.workflow_patterns.merge.Merge
import pl.edu.agh.workflow_patterns.synchronization.{Sync, MultipleSync}

object WorkFlowDsl {

  implicit class SourceDataToWorkflow(source: Source) {
    /*def ~>(workflow: Workflow) = {
      source.data.foreach { d =>
        workflow.in.data :+= d
      }
    }*/
    def ~>(in: In[Int]) = {
      source.data.foreach { d =>
        in.data :+= d
      }
    }
  }

  implicit class StrSourceDataToWorkflow(source: StringSource) {
    /*def ~>(workflow: Workflow) = {
      source.data.foreach { d =>
        workflow.in.data :+= d
      }
    }*/
    def ~>(in: In[String]) = {
      source.data.foreach { d =>
        in.data :+= d
      }
    }
  }

  implicit class InputDataToNext[T](in: In[T]) {
    def ~>>[K](elem: Sync[T, K]) = {
      in.data.foreach { d =>
        elem.actor ! DataMessage(d)
      }
    }
    def ~>>[K](elem: Pattern[T, K]) = {
      PropagateDataActor(in.data) ! PropagateData(elem)
    }
  }

  implicit class TwoInputsDataToNext[T](ins: (In[T], In[T])) {
    def ~>>[K](elem: MultipleSync[T, K]) = {
      PropagateDataForMultipleSyncActor(ins._1.data) ! PropagateDataForMultipleSync(elem, 0)
      PropagateDataForMultipleSyncActor(ins._2.data) ! PropagateDataForMultipleSync(elem, 1)
    }
  }

  implicit class ThreeInputsDataToNext[T](ins: (In[T], In[T], In[T])) {
    def ~>>[K](elem: MultipleSync[T, K]) = {
      PropagateDataForMultipleSyncActor(ins._1.data) ! PropagateDataForMultipleSync(elem, 0)
      PropagateDataForMultipleSyncActor(ins._2.data) ! PropagateDataForMultipleSync(elem, 1)
      PropagateDataForMultipleSyncActor(ins._2.data) ! PropagateDataForMultipleSync(elem, 2)
    }
  }

  implicit class FourInputsDataToNext[T](ins: (In[T], In[T], In[T], In[T])) {
    def ~>>[K](elem: MultipleSync[T, K]) = {
      PropagateDataForMultipleSyncActor(ins._1.data) ! PropagateDataForMultipleSync(elem, 0)
      PropagateDataForMultipleSyncActor(ins._2.data) ! PropagateDataForMultipleSync(elem, 1)
      PropagateDataForMultipleSyncActor(ins._2.data) ! PropagateDataForMultipleSync(elem, 2)
      PropagateDataForMultipleSyncActor(ins._2.data) ! PropagateDataForMultipleSync(elem, 3)
    }
  }

  implicit class ResultToNext[K](data: List[K]) {
    def ~>[T](elem: Merge[T, K]) = {
      PropagateDataActor(data) ! PropagateData(elem)
    }
    def ~>[T](elem: Pattern[T, K]) = {
      data.foreach { d =>
        elem.actor ! DataMessage(d)
      }
    }
    def ~>>(out: Out[K]) = {
      var outRes = out.result
      data.foreach { d =>
        outRes :+= d
      }
      out.result = outRes
      out
    }
  }

  implicit class ListBufferToNext[K](sink: ActorRef) {
    def ~>>(out: Out[K]) = {

      val data = SinkUtils.getResults[K](sink)

      var outRes = out.result
      data.foreach { d =>
        outRes :+= d
      }
      out.result = outRes
      out

    }
  }

  implicit class ForwardIteratorDataToNext[K](data: Iterator[List[K]]) {
    def ~>[T](elem: Pattern[T, K]) = {
      data.toList.foreach { d =>
        elem.actor ! DataMessage(d)
      }
      elem
    }
  }

  implicit class ForwardListOfListsDataToNext[K](data: List[List[K]]) {
    def ~>[T](elem: Pattern[T, K]) = {
      data.foreach { d =>
        elem.actor ! DataMessage(d)
      }
      elem
    }
  }

  /*implicit object Send {
    def ->[T](action: ISingleAction[T]) = {
      action
    }
    def ->[T](action: IMultipleAction[T]) = {
      action
    }
  }

  def send[T](action: ISingleAction[T]) = action
  def send[T](action: IMultipleAction[T]) = action*/

}
