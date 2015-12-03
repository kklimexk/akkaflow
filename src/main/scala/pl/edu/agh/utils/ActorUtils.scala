package pl.edu.agh.utils

import akka.actor.{ActorSystem, ActorRef}
import akka.util.Timeout
import akka.pattern.ask

import pl.edu.agh.messages.Get
import pl.edu.agh.workflow_patterns.synchronization.{Sync, SyncActor}

import scala.concurrent.Await
import scala.concurrent.duration._

object ActorUtils {

  val system = ActorSystem("ActorSystem")
  implicit val timeout = Timeout(5 seconds)

  implicit class ConverterToActor(actorRef: ActorRef) {
    def toSyncActor[T]: SyncActor[T] = {
      val actorF = actorRef ? Get
      val res = Await.result(actorF, timeout.duration).asInstanceOf[SyncActor[T]]
      res
    }
    def out: List[Int] = {
      val actor = actorRef.toSyncActor
      actor.out
    }
  }
  implicit def convertSyncToSyncActor[T](sync: Sync[T]): SyncActor[T] = sync.syncActor.toSyncActor
}
