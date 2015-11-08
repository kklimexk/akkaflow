package pl.edu.agh.main

import akka.actor.ActorSystem
import pl.edu.agh.dsl.WorkFlowDsl
import pl.edu.agh.flows.{OutChannel, InChannel}
import pl.edu.agh.messages.IntDataMessage
import pl.edu.agh.workflow_patterns.synchronization.SynchronizationPattern

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object SynchronizationMain extends App {
  import WorkFlowDsl._

  implicit val system = ActorSystem("SynchronizationPatternSystem")

  val in1 = InChannel(IntDataMessage(2))
  val in2 = InChannel(IntDataMessage(3))
  val A1 = SynchronizationPattern(system, name = "A1")
  val out = OutChannel(system)

  (in1, in2) ~> A1 ~> out

  Await.result(system.whenTerminated, Duration.Inf)
}
