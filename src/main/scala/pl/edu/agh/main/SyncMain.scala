package pl.edu.agh.main

import pl.edu.agh.actions.Action
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.flows.Source
import pl.edu.agh.utils.ActorUtils._
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.workflow_patterns.synchronization._

import scala.concurrent.Await
import scala.concurrent.duration._

object SyncMain extends App {

  val sqr = Action[Int] {
    in => in * in
  }

  val sum = Action[List[Int]] {
    in => in.reduceLeft[Int](_+_)
  }

  val sqrProc = Sync[Int] {
    send (sqr)
  }

  val sumProc = Sync[List[Int]] {
    send (sum)
  }

  def w = Workflow { () =>
    Source(1 to 6) ~> sqrProc
    //println(sqrProc.out)
    sqrProc.out.grouped(3) ~> sumProc
    sumProc.out
  }

  val res = w.run
  println(res)

  Await.result(system.whenTerminated, Duration.Inf)
}
