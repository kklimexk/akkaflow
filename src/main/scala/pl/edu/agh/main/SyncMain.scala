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

  val sqr = Action[Int] { in =>
    in * in
  }

  val sum = Action[List[Int]] { in =>
    in.reduceLeft[Int](_+_)
  }

  val sqrProc = Sync {
    send (sqr)
  }

  val sumProc = Sync {
    send (sum)
  }

  val w = Workflow { (in, out) =>
    in ~>> sqrProc
    sqrProc.out.grouped(3) ~> sumProc
    sumProc.out ~>> out
  }

  Source(1 to 6) ~> w
  val res = w.run
  println(res)

  Await.result(system.whenTerminated, Duration.Inf)
}
