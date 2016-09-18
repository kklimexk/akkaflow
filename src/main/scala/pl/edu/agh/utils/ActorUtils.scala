package pl.edu.agh.utils

import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import akka.pattern.ask
import pl.edu.agh.messages.Get
import pl.edu.agh.workflow.elements.Sink
import pl.edu.agh.workflow_processes.PatternActor
import pl.edu.agh.workflow_processes.discriminator.{Disc, DiscActor}
import pl.edu.agh.workflow_processes.simple.{Process, ProcessActor}
import pl.edu.agh.workflow_processes.synchronization.{Sync, SyncActor}

import scala.concurrent.Await
import scala.concurrent.duration._

object ActorUtils {

  val system = ActorSystem("ActorSystem")

  object Implicits {

    implicit val maxTimeForRes = 100
    implicit val timeout = Timeout(20 seconds)

    implicit class ConverterToActor(actorRef: ActorRef) {

      lazy val actorF = actorRef ? Get

      def toPatternActor: PatternActor = {
        Await.result(actorF, timeout.duration).asInstanceOf[PatternActor]
      }

      def toSyncActor[T, R]: SyncActor[T, R] = {
        Await.result(actorF, timeout.duration).asInstanceOf[SyncActor[T, R]]
      }

      def toProcessActor[T, R]: ProcessActor[T, R] = {
        Await.result(actorF, timeout.duration).asInstanceOf[ProcessActor[T, R]]
      }

      def toDiscActor[T, R]: DiscActor[T, R] = {
        Await.result(actorF, timeout.duration).asInstanceOf[DiscActor[T, R]]
      }

      def toSinkActor[R]: Sink[R] = {
        Await.result(actorF, timeout.duration).asInstanceOf[Sink[R]]
      }

      /*def out: List[Int] = {
        val actor = actorRef.toWorkflowProcess
        actor.out
      }*/

      /*def outs: List[List[Int]] = {
        val actor = actorRef.toChoiceActor
        List(actor.out1, actor.out2, actor.out3)
      }*/

    }
    implicit def convertSyncToSyncActor[T, R](mSync: Sync[T, R]): SyncActor[T, R] = mSync.actor.toSyncActor
    implicit def convertProcessToProcessActor[T, R](merge: Process[T, R]): ProcessActor[T, R] = merge.actor.toProcessActor
    implicit def convertDiscToDiscActor[T, R](disc: Disc[T, R]): DiscActor[T, R] = disc.actor.toDiscActor
  }
}
