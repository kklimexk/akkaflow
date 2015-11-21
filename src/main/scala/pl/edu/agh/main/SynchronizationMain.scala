package pl.edu.agh.main

import akka.actor.ActorSystem
import pl.edu.agh.dsl.WorkFlowDsl
import pl.edu.agh.flows.{OutChannel, InChannel}
import pl.edu.agh.messages.DataMessage
import pl.edu.agh.workflow_patterns.synchronization.Sync

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object SynchronizationMain extends App {
  import WorkFlowDsl._

  implicit val system = ActorSystem("SynchronizationPatternSystem")

  val in1 = InChannel(DataMessage(2))
  val in2 = InChannel(DataMessage(3))
  val A1 = Sync(system, name = "A1")
  val out = OutChannel(system, name = "out")

  (in1, in2) ~> A1 ~> out

  Await.result(system.whenTerminated, Duration.Inf)
}
