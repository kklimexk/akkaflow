package pl.edu.agh.utils

import akka.actor.{ActorSystem, ActorRef}
import akka.util.Timeout
import akka.pattern.ask

import pl.edu.agh.messages.Get
import pl.edu.agh.workflow_patterns.WorkflowProcess
import pl.edu.agh.workflow_patterns.choice.{Choice, ChoiceActor}
import pl.edu.agh.workflow_patterns.merge.{MergeActor, Merge}
import pl.edu.agh.workflow_patterns.synchronization.{MultipleSync, MultipleSyncActor, Sync, SyncActor}

import scala.concurrent.Await
import scala.concurrent.duration._

object ActorUtils {

  val system = ActorSystem("ActorSystem")
  implicit val timeout = Timeout(5 seconds)

  implicit class ConverterToActor(actorRef: ActorRef) {

    lazy val actorF = actorRef ? Get

    def toWorkflowProcess: WorkflowProcess = {
      Await.result(actorF, timeout.duration).asInstanceOf[WorkflowProcess]
    }

    def toSyncActor[T, K]: SyncActor[T, K] = {
      Await.result(actorF, timeout.duration).asInstanceOf[SyncActor[T, K]]
    }

    def toMultipleSyncActor[T, K]: MultipleSyncActor[T, K] = {
      Await.result(actorF, timeout.duration).asInstanceOf[MultipleSyncActor[T, K]]
    }

    def toChoiceActor[T, K]: ChoiceActor[T, K] = {
      Await.result(actorF, timeout.duration).asInstanceOf[ChoiceActor[T, K]]
    }

    def toMergeActor[T, K]: MergeActor[T, K] = {
      Await.result(actorF, timeout.duration).asInstanceOf[MergeActor[T, K]]
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
  implicit def convertSyncToSyncActor[T, K](sync: Sync[T, K]): SyncActor[T, K] = sync.syncActor.toSyncActor
  implicit def convertMultipleSyncToMultipleSyncActor[T, K](mSync: MultipleSync[T, K]): MultipleSyncActor[T, K] = mSync.syncActor.toMultipleSyncActor
  implicit def convertChoiceToChoiceActor[T, K](choice: Choice[T, K]): ChoiceActor[T, K] = choice.choiceActor.toChoiceActor
  implicit def convertMergeToMergeActor[T, K](merge: Merge[T, K]): MergeActor[T, K] = merge.mergeActor.toMergeActor
}
