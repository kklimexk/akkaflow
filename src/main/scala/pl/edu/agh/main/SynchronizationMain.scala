package pl.edu.agh.main

import pl.edu.agh.actions.Action
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.flows.Source
import pl.edu.agh.utils.ActorUtils._
import pl.edu.agh.workflow_patterns.synchronization.Sync

import scala.concurrent.Await
import scala.concurrent.duration._

object SynchronizationMain extends App {

  //val in1 = InChannel(DataMessage(2))
  //val in2 = InChannel(DataMessage(3))
  val sqr = Action[Int] {
    in => in * in
  }

  val sum = Action[List[Int]] {
    in => in.reduceLeft[Int](_+_)
  }

  val sqrProc = Sync(name = "sqrProc", sqr)
  val sumProc = Sync(name = "sumProc", sum)

  Source(1 to 6) ~> sqrProc
  println(sqrProc.out)
  sqrProc.out.grouped(3) ~> sumProc
  println(sumProc.out)
  Await.result(system.whenTerminated, Duration.Inf)
}
