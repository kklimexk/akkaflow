package pl.edu.agh.utils

import akka.actor.ActorRef
import akka.pattern.ask
import pl.edu.agh.messages.{GetGroupedOut, GetOut}
import pl.edu.agh.utils.FSMStates._
import pl.edu.agh.workflow.elements._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SinkUtils {
  //lazy val awaitTime = ConfigFactory.load().getInt("config.state.awaitTime")

  def getResultsAsync[R](sink: ActorRef): Future[List[R]] = {
    import pl.edu.agh.utils.ActorUtils.Implicits._

    def checkSinkState(sink: ActorRef, sinkActor: Sink[R]): Future[List[R]] = {
      val res = Future.successful(sinkActor.stateName).flatMap {
        case Idle => (sink ? GetOut).flatMap {
          case NotReady =>
            checkSinkState(sink, sinkActor)
          case result: List[R] => Future.successful(result)
        }
        case activeOrInit @ (Active | Init) =>
          checkSinkState(sink, sinkActor)
      }
      res
    }
    checkSinkState(sink, sink.toSinkActor)
  }
  def getGroupedResultsAsync[R](sink: ActorRef)(size: Int) = {
    import pl.edu.agh.utils.ActorUtils.Implicits._

    def checkSinkState(sink: ActorRef, sinkActor: Sink[R]): Future[Iterator[List[R]]] = {
      val res = Future.successful(sinkActor.stateName).flatMap {
        case Idle => (sink ? GetGroupedOut(size)).flatMap {
          case NotReady =>
            checkSinkState(sink, sinkActor)
          case result: Iterator[List[R]] => Future.successful(result)
        }
        case activeOrInit @ (Active | Init) =>
          checkSinkState(sink, sinkActor)
      }
      res
    }
    checkSinkState(sink, sink.toSinkActor)
  }
}
