package pl.edu.agh.utils

import akka.actor.ActorRef
import akka.util.Timeout
import akka.pattern.ask

import pl.edu.agh.messages.Get
import pl.edu.agh.workflow_patterns.synchronization.Sync

import scala.concurrent.Await
import scala.concurrent.duration._

object ActorUtils {
  implicit val timeout = Timeout(5 seconds)

  implicit class ConverterToActor(actorRef: ActorRef) {
    def toSync: Sync = {
      val actorF = actorRef ? Get
      val res = Await.result(actorF, timeout.duration).asInstanceOf[Sync]
      res
    }
    def out: List[Int] = {
      val actor = actorRef.toSync
      actor.out
    }
  }
}
