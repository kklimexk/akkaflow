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
    def ~>(in: In[Int]) = {
      source.data.foreach { d =>
        in.data :+= d
      }
    }
  }

  implicit class StrSourceDataToWorkflow(source: StringSource) {
    def ~>(in: In[String]) = {
      source.data.foreach { d =>
        in.data :+= d
      }
    }
  }

  implicit class AnyValSourceDataToWorkflow[T <: AnyVal](source: AnyValSource[T]) {
    def ~>(in: In[T]) = {
      source.data.foreach { d =>
        in.data :+= d
      }
    }
  }

  object MSyncId {
    var uniqueId = 0
  }

  implicit class InputDataToNext[T](in: In[T]) {
    def ~>>[K](elem: Pattern[T, K]) = {
      elem match {
        case e: MultipleSync[T, K] =>
          PropagateDataForMultipleSyncActor(in.data) ! PropagateDataForMultipleSync(e, MSyncId.uniqueId)
          MSyncId.uniqueId += 1
        case _ => PropagateDataActor(in.data) ! PropagateData(elem)
      }
    }
  }

  object DataState {
    var dataList = List.empty[Future[Any]]
    var outs = List.empty[Future[Any]]
  }

  implicit class DataFromSinkToNext[K](sink: ActorRef) {
    def grouped[T](size: Int) = {
      val dataIterF = SinkUtils.getGroupedResultsAsync[T](sink)(size)
      dataIterF
    }
    def ~>[T](elem: Pattern[T, K]) = {
      var dataF = SinkUtils.getResultsAsync[K](sink)
      dataF onSuccess {
        case data: List[K] => elem match {
          case e: MultipleSync[T, K] =>
            PropagateDataForMultipleSyncActor(data) ! PropagateDataForMultipleSync(e, MSyncId.uniqueId)
            MSyncId.uniqueId += 1
          case _ => PropagateDataActor(data) ! PropagateData(elem)
        }
      }
      DataState.dataList :+= dataF
    }
    def ~>>(out: Out[K]) = {
      val futureL = Future.sequence(DataState.dataList)
      Await.ready(futureL, Duration.Inf)

      val dataF = SinkUtils.getResultsAsync[K](sink)

      val outF = dataF.map(data => {
        var outRes = out.result
        data.foreach { d =>
          outRes :+= d
        }
        out.result = outRes
        out
      })

      DataState.outs :+= outF
      Future.sequence(DataState.outs).mapTo[List[Out[K]]]
    }
  }

  implicit class ForwardIteratorDataToNext[K](dataF: Future[Iterator[List[K]]]) {
    def ~>[T](elem: Pattern[T, K]) = {
      DataState.dataList :+= dataF
      dataF onSuccess {
        case data: Iterator[List[K]] => PropagateDataActor(data) ! PropagateData(elem)
      }
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
