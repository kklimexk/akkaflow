package pl.edu.agh.utils

import akka.actor.ActorRef
import akka.pattern.ask
import com.typesafe.config.ConfigFactory
import pl.edu.agh.messages.{GetGroupedOut, GetOut}
import pl.edu.agh.utils.FSMStates._
import pl.edu.agh.workflow.elements._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object SinkUtils {
  lazy val awaitTime = ConfigFactory.load().getInt("sink.state.awaitTime")

  def getResultsAsync[R](sink: ActorRef): Future[List[R]] = {
    import pl.edu.agh.utils.ActorUtils.Implicits._
    import pl.edu.agh.utils.ActorUtils._

    def checkSinkState(sink: ActorRef, sinkActor: Sink[R]): Future[List[R]] = {
      val res = Future.successful(sinkActor.stateName).flatMap {
        case Idle => (sink ? GetOut).mapTo[List[R]]
        case activeOrInit @ (Active | Init) =>
          akka.pattern.after(awaitTime milliseconds, using = system.scheduler)(checkSinkState(sink, sinkActor))
      }
      res
    }
    checkSinkState(sink, sink.toSinkActor)
  }
  def getGroupedResultsAsync[R](sink: ActorRef)(size: Int) = {
    import pl.edu.agh.utils.ActorUtils.Implicits._
    import pl.edu.agh.utils.ActorUtils._

    def checkSinkState(sink: ActorRef, sinkActor: Sink[R]): Future[Iterator[List[R]]] = {
      val res = Future.successful(sinkActor.stateName).flatMap {
        case Idle => (sink ? GetGroupedOut(size)).mapTo[Iterator[List[R]]]
        case activeOrInit @ (Active | Init) =>
          akka.pattern.after(awaitTime milliseconds, using = system.scheduler)(checkSinkState(sink, sinkActor))
      }
      res
    }
    checkSinkState(sink, sink.toSinkActor)
  }
}
