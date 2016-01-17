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

    def toSyncActor[T]: SyncActor[T] = {
      Await.result(actorF, timeout.duration).asInstanceOf[SyncActor[T]]
    }

    def toMultipleSyncActor[T]: MultipleSyncActor[T] = {
      Await.result(actorF, timeout.duration).asInstanceOf[MultipleSyncActor[T]]
    }

    def toChoiceActor[T]: ChoiceActor[T] = {
      Await.result(actorF, timeout.duration).asInstanceOf[ChoiceActor[T]]
    }

    def toMergeActor[T]: MergeActor[T] = {
      Await.result(actorF, timeout.duration).asInstanceOf[MergeActor[T]]
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
  implicit def convertSyncToSyncActor[T](sync: Sync[T]): SyncActor[T] = sync.syncActor.toSyncActor
  implicit def convertMultipleSyncToMultipleSyncActor[T](mSync: MultipleSync[T]): MultipleSyncActor[T] = mSync.syncActor.toMultipleSyncActor
  implicit def convertChoiceToChoiceActor[T](choice: Choice[T]): ChoiceActor[T] = choice.choiceActor.toChoiceActor
  implicit def convertMergeToMergeActor[T](merge: Merge[T]): MergeActor[T] = merge.mergeActor.toMergeActor
}
