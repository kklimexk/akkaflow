package pl.edu.agh.dsl

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.ActorRef
import pl.edu.agh.data_propagators.{PropagateDataActor, PropagateDataForSyncActor}
import pl.edu.agh.messages._
import pl.edu.agh.utils.SinkUtils
import pl.edu.agh.workflow.elements._
import pl.edu.agh.workflow.{IWorkflow, Workflow}
import pl.edu.agh.workflow_processes.{IPattern, Pattern}
import pl.edu.agh.workflow_processes.synchronization.Sync

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object WorkFlowDsl {

  object SourceDataState {
    var sourceDataFList = List.empty[Future[Any]]
  }

  implicit class SourceDataToWorkflow(source: Source) {
    def ~>(in: In[Int]) = {
      val f = Future {
        source.data.foreach { d =>
          in.data :+= d
        }
      }
      SourceDataState.sourceDataFList :+= f
    }
    def =>>(in: In[Int])(implicit w: IWorkflow) = {
      val workflow = w.asInstanceOf[Workflow[Any, Any]]
      workflow.clearIns()

      //in.data = List.empty[Int]
      source.data.foreach { d =>
        in.data :+= d
      }

      val resF = workflow.block(workflow.ins, workflow.outs)
      Await.ready(resF, Duration.Inf)
    }
  }

  implicit class StrSourceDataToWorkflow(source: StringSource) {
    def ~>(in: In[String]) = {
      val f = Future {
        source.data.foreach { d =>
          in.data :+= d
        }
      }
      SourceDataState.sourceDataFList :+= f
    }
    def =>>(in: In[String])(implicit w: IWorkflow) = {
      val workflow = w.asInstanceOf[Workflow[Any, Any]]
      workflow.clearIns()

      //in.data = List.empty[String]
      source.data.foreach { d =>
        in.data :+= d
      }

      val resF = workflow.block(workflow.ins, workflow.outs)
      Await.ready(resF, Duration.Inf)
    }
  }

  implicit class AnyRangeSourceDataToWorkflow(source: AnyRangeSource) {
    def ~>(in: In[Any]) = {
      val f = Future {
        source.data.foreach { d =>
          in.data :+= d
        }
      }
      SourceDataState.sourceDataFList :+= f
    }
    def =>>(in: In[Any])(implicit w: IWorkflow) = {
      val workflow = w.asInstanceOf[Workflow[Any, Any]]
      workflow.clearIns()

      //in.data = List.empty[Any]
      source.data.foreach { d =>
        in.data :+= d
      }

      val resF = workflow.block(workflow.ins, workflow.outs)
      Await.ready(resF, Duration.Inf)
    }
  }

  implicit class AnySourceDataToWorkflow(source: AnySource) {
    def ~>(in: In[Any]) = {
      val f = Future {
        source.data.foreach { d =>
          in.data :+= d
        }
      }
      SourceDataState.sourceDataFList :+= f
    }
    def =>>(in: In[Any])(implicit w: IWorkflow) = {
      val workflow = w.asInstanceOf[Workflow[Any, Any]]
      workflow.clearIns()

      //in.data = List.empty[Any]
      source.data.foreach { d =>
        in.data :+= d
      }

      val resF = workflow.block(workflow.ins, workflow.outs)
      Await.ready(resF, Duration.Inf)
    }
  }

  implicit class ParametrizedSourceDataToWorkflow[T](source: ParametrizedSource[T]) {
    def ~>(in: In[T]) = {
      val f = Future {
        source.data.foreach { d =>
          in.data :+= d
        }
      }
      SourceDataState.sourceDataFList :+= f
    }
    def =>>(in: In[T])(implicit w: IWorkflow) = {
      val workflow = w.asInstanceOf[Workflow[Any, Any]]
      workflow.clearIns()

      //in.data = List.empty[Any]
      source.data.foreach { d =>
        in.data :+= d
      }

      val resF = workflow.block(workflow.ins, workflow.outs)
      Await.ready(resF, Duration.Inf)
    }
  }

  //-------------------------------------------------------------------

  object MSyncId {
    var uniqueId = new AtomicInteger(0)
    def resetUniqueId = uniqueId = new AtomicInteger(0)
  }

  implicit class InputDataToNext[T](in: In[T]) {
    def ~>>[R](elem: Pattern[T, R]) = {
      val f = Future.sequence(SourceDataState.sourceDataFList)
      if (!f.isCompleted) Await.ready(f, Duration.Inf)
      elem match {
        case e: Sync[T, R] =>
          PropagateDataForSyncActor(in.data) ! PropagateDataForSync(e, MSyncId.uniqueId.getAndIncrement())
        case _ => PropagateDataActor(in.data) ! PropagateData(elem)
      }
    }
    def ~>[R](elem: Pattern[T, R]) = {
      if (DataState.prevPattern.isDefined && DataState.prevPattern.get != elem) {
        MSyncId.resetUniqueId
        val futureL = Future.sequence(DataState.dataList)
        Await.ready(futureL, Duration.Inf)
      }
      elem match {
        case e: Sync[T, R] =>
          PropagateDataForSyncActor(in.data) ! PropagateDataForSync(e, MSyncId.uniqueId.getAndIncrement())
        case _ => PropagateDataActor(in.data) ! PropagateData(elem)
      }
      DataState.prevPattern = Some(elem)
    }
  }

  //-------------------------------------------------------------------

  object DataState {
    var dataList = List.empty[Future[Any]]
    var outs = List.empty[Future[Any]]
    var prevPattern: Option[IPattern] = None
  }

  implicit class DataFromSinkToNext[R](sink: ActorRef) {
    def grouped[T](size: Int) = {
      val dataIterF = SinkUtils.getGroupedResultsAsync[T](sink)(size)
      dataIterF
    }

    def ~>[T](elem: Pattern[T, R]) = {
      if (DataState.prevPattern.isDefined && DataState.prevPattern.get != elem) {
        MSyncId.resetUniqueId
        val futureL = Future.sequence(DataState.dataList)
        Await.ready(futureL, Duration.Inf)
      }
      var dataF = SinkUtils.getResultsAsync[R](sink)
      dataF onSuccess {
        case data: List[R] => elem match {
          case e: Sync[T, R] =>
            PropagateDataForSyncActor(data) ! PropagateDataForSync(e, MSyncId.uniqueId.getAndIncrement())
          case _ =>
            PropagateDataActor(data) ! PropagateData(elem)
        }
      }
      DataState.prevPattern = Some(elem)
      DataState.dataList :+= dataF
    }

    def ~>>(out: Out[R]) = {
      val futureL = Future.sequence(DataState.dataList)
      Await.ready(futureL, Duration.Inf)

      val dataF = SinkUtils.getResultsAsync[R](sink)

      val outF = dataF.map(data => {
        var outRes = out.result
        data.foreach { d =>
          outRes :+= d
        }
        out.result = outRes
        out
      })

      MSyncId.resetUniqueId
      DataState.outs :+= outF
      Future.sequence(DataState.outs).mapTo[List[Out[R]]]
    }
  }

  implicit class ForwardIteratorDataToNext[R](dataF: Future[Iterator[List[R]]]) {
    def ~>[T](elem: Pattern[T, R]) = {
      if (DataState.prevPattern.isDefined && DataState.prevPattern.get != elem) {
        MSyncId.resetUniqueId
        val futureL = Future.sequence(DataState.dataList)
        Await.ready(futureL, Duration.Inf)
      }
      dataF onSuccess {
        case data: Iterator[List[R]] => PropagateDataActor(data) ! PropagateData(elem)
      }
      DataState.prevPattern = Some(elem)
      DataState.dataList :+= dataF
    }
  }

}
