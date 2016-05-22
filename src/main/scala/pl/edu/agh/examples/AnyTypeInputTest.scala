package pl.edu.agh.examples

import pl.edu.agh.workflow.Workflow
import pl.edu.agh.dsl.WorkFlowDsl._
import pl.edu.agh.actions.ActionDsl.Implicits._
import pl.edu.agh.actions.{Ins, Outs}
import pl.edu.agh.workflow.elements.{AnyRangeSource, AnySource, In, Out}
import pl.edu.agh.workflow_processes._
import pl.edu.agh.workflow_processes.simple.ProcessDsl._
import pl.edu.agh.workflow_processes.synchronization.SyncDsl._

/** Prosty test majacy sprawdzic czy mozna uzyc roznych typow danych dla wejsc */
object AnyTypeInputTest extends App {

  val sum = { in: Seq[Any] => implicit outs: Outs =>
    val res = in match {
      case i: List[Double] => i.reduceLeft[Double](_+_)
      case i: List[Int] => i.reduceLeft[Int](_+_)
      case _ => identity(in)
    }
    res =>> "secondOut"
  }

  val sumOnlyNumbers = { ins: Ins[Any] => implicit outs: Outs =>
    val res = (ins("firstIn"), ins("secondIn"), ins("thirdIn")) match {
      case (i1: Int, i2: Int, i3: Int) => i1 + i2 + i3
      case (i1: Int, i2: Double, i3: Int) => i1 + i2 + i3
      case (i1: Int, i2: Int, i3: Double) => i1 + i2 + i3
      case (i1: Int, i2: String, i3: Double) => i1 + i3
      case _ => identity(ins("firstIn"), ins("secondIn"), ins("thirdIn"))
    }
    def foo(d: Any) = d.asInstanceOf[Int].toDouble

    val res2 = foo(ins("firstIn"))

    res =>> "sumOut_1"
    res2 =>> "sumOut_2"
  }

  val sumSyncProc = Sync[Any, Any]
    .name("sumProc")
    .inputs("firstIn", "secondIn", "thirdIn")
    .outputs("sumOut_1", "sumOut_2")
    .action(sumOnlyNumbers)

  val mergeProc = Process[Seq[Any], Any] (
    name = "mergeProc",
    outs = ("firstOut", "secondOut"),
    action = sum
  )

  val splitProc = Process[Any, Any]
    .name("splitProc")
    .numOfOuts(3)
    .action { (in: Any, outs: Outs) => outs().foreach(out => in =>> out) }

  val w = Workflow (
    "Any type input test",
    numOfIns = 3,
    numOfOuts = 3,
    (ins: Seq[In[Any]], outs: Seq[Out[Any]]) => {
      import pl.edu.agh.utils.ActorUtils.Implicits._

      ins(0) ~>> sumSyncProc
      ins(1) ~>> sumSyncProc
      ins(2) ~>> sumSyncProc

      sumSyncProc.outs("sumOut_1").grouped(10) ~> mergeProc
      sumSyncProc.outs("sumOut_2").grouped(10) ~> mergeProc

      mergeProc.outs("secondOut") ~> splitProc

      splitProc.outs(0) ~>> outs(0)
      splitProc.outs(1) ~>> outs(1)
      splitProc.outs(2) ~>> outs(2)
    }
  )

  AnyRangeSource(1 to 10) ~> w.ins(0)
  AnySource("java", "scala", "akka", "drzewo", "lato", 1, 2, 3, 4, 5) ~> w.ins(1)
  AnySource(1.1, 2.2, 3.3, 4.4, 5.5, 6.6, 7.7, 8.8, 9.9, 100.0) ~> w.ins(2)

  val res = w.run
  println(res)
  println(w)
}
