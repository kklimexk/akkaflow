package pl.edu.agh.main

import akka.actor.ActorSystem

import pl.edu.agh.dsl.WorkFlowDsl
import pl.edu.agh.flows.OutChannel
import pl.edu.agh.utils.ActorUtils
import pl.edu.agh.workflow_patterns.synchronization.Sync

import scala.concurrent.Await
import scala.concurrent.duration._

object SynchronizationMain extends App {
  import WorkFlowDsl._
  import ActorUtils._

  implicit val system = ActorSystem("SynchronizationPatternSystem")

  //val in1 = InChannel(DataMessage(2))
  //val in2 = InChannel(DataMessage(3))
  val sqrProc = Sync(name = "sqrProc")
  val sumProc = Sync(name = "sumProc")
  val out = OutChannel(name = "out")

  (1 to 10) ~> sqrProc
  println(sqrProc.out)
  //sqrProc.out.grouped(3) ~> sumProc

  Await.result(system.whenTerminated, Duration.Inf)
}
