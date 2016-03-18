package pl.edu.agh.utils

import akka.actor.ActorRef
import pl.edu.agh.messages.{GetGroupedOut, GetOut}

import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.ask

object SinkUtils {
  import pl.edu.agh.utils.ActorUtils.{system, timeout}

  def getResults[R](sink: ActorRef) = {
    val dataF = akka.pattern.after(200 milliseconds, using = system.scheduler)(sink ? GetOut)
    val data = Await.result(dataF, Duration.Inf).asInstanceOf[List[R]]

    data
  }
  def getGroupedResults[R](sink: ActorRef)(size: Int) = {
    val dataF = akka.pattern.after(200 milliseconds, using = system.scheduler)(sink ? GetGroupedOut(size))
    val data = Await.result(dataF, Duration.Inf).asInstanceOf[Iterator[List[R]]]

    data
  }
  def getResultsAsync[R](sink: ActorRef) = {
    def f = Future {
      var res = List.empty[R]
      var time = 0
      var resultsNotReady = true
      while (resultsNotReady) {
        val f = akka.pattern.after(time milliseconds, using = system.scheduler)(sink ? GetOut)
        Await.result(f, Duration.Inf) match {
          case None => time += 10
          case r: R => res :+= r; time = 0
        }
        if (time >= 100) resultsNotReady = false
      }
      res
    }
    f
  }
  def getGroupedResultsAsync[R](sink: ActorRef)(size: Int) = {
    def f = Future {
      var res = List.empty[List[R]]
      var time = 0
      var resultsNotReady = true
      while (resultsNotReady) {
        val f = akka.pattern.after(time milliseconds, using = system.scheduler)(sink ? GetGroupedOut(size))
        Await.result(f, Duration.Inf) match {
          case None => time += 10
          case r: List[R] => res :+= r; time = 0
        }
        if (time >= 100) resultsNotReady = false
      }
      res
    }
    f
  }
}
