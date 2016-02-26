package pl.edu.agh.dsl

import akka.actor.ActorRef
import pl.edu.agh.data_propagators.{PropagateDataForMultipleSyncActor, PropagateDataActor}
import pl.edu.agh.flows._
import pl.edu.agh.messages._
import pl.edu.agh.utils.SinkUtils
import pl.edu.agh.workflow_patterns.Pattern
import pl.edu.agh.workflow_patterns.synchronization.MultipleSync

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

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

  object DataState {
    var dataList = List.empty[Future[Any]]
  }

  implicit class DataFromSinkToNext[K](sink: ActorRef) {
    def grouped[T](size: Int) = {
      val dataIter = SinkUtils.getGroupedResults[T](sink)(size)
      dataIter
    }
    def ~>[T](elem: Pattern[T, K]) = {
      var dataF = SinkUtils.getResultsAsync[K](sink)
      DataState.dataList :+= dataF
      dataF onSuccess {
        case data: List[K] => PropagateDataActor(data) ! PropagateData(elem)
      }
    }
    def ~>>(out: Out[K]) = {

      val futureL = Future.sequence(DataState.dataList)
      Await.ready(futureL, Duration.Inf)

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
      PropagateDataActor(data) ! PropagateData(elem)
    }
  }

  @deprecated("Now patterns contain Sink actors")
  implicit class ResultToNext[K](data: List[K]) {
    def ~>[T](elem: Pattern[T, K]) = {
      PropagateDataActor(data) ! PropagateData(elem)
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
