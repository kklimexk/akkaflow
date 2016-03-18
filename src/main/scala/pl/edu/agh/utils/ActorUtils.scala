package pl.edu.agh.utils

import akka.actor.{ActorSystem, ActorRef}
import akka.util.Timeout
import akka.pattern.ask

import pl.edu.agh.messages.Get
import pl.edu.agh.workflow_patterns.PatternActor
import pl.edu.agh.workflow_patterns.choice.{Choice, ChoiceActor}
import pl.edu.agh.workflow_patterns.merge.{MergeActor, Merge}
import pl.edu.agh.workflow_patterns.split.{Split, SplitActor}
import pl.edu.agh.workflow_patterns.synchronization.{Sync, SyncActor}

import scala.concurrent.Await
import scala.concurrent.duration._

object ActorUtils {

  val system = ActorSystem("ActorSystem")

  object Implicits {

    implicit val maxTimeForRes = 100
    implicit val timeout = Timeout(5 seconds)

    implicit class ConverterToActor(actorRef: ActorRef) {

      lazy val actorF = actorRef ? Get

      def toPatternActor: PatternActor = {
        Await.result(actorF, timeout.duration).asInstanceOf[PatternActor]
      }

      def toSyncActor[T, R]: SyncActor[T, R] = {
        Await.result(actorF, timeout.duration).asInstanceOf[SyncActor[T, R]]
      }

      def toChoiceActor[T, R]: ChoiceActor[T, R] = {
        Await.result(actorF, timeout.duration).asInstanceOf[ChoiceActor[T, R]]
      }

      def toMergeActor[T, R]: MergeActor[T, R] = {
        Await.result(actorF, timeout.duration).asInstanceOf[MergeActor[T, R]]
      }

      def toSplitActor[T, R]: SplitActor[T, R] = {
        Await.result(actorF, timeout.duration).asInstanceOf[SplitActor[T, R]]
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
    implicit def convertChoiceToChoiceActor[T, R](choice: Choice[T, R]): ChoiceActor[T, R] = choice.actor.toChoiceActor
    implicit def convertMergeToMergeActor[T, R](merge: Merge[T, R]): MergeActor[T, R] = merge.actor.toMergeActor
    implicit def convertSplitToSplitActor[T, R](split: Split[T, R]): SplitActor[T, R] = split.actor.toSplitActor
  }
}
