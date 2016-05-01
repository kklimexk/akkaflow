package pl.edu.agh.main

import pl.edu.agh.actions.Outs
import pl.edu.agh.flows.{In, Out, Source}
import pl.edu.agh.workflow.Workflow
import pl.edu.agh.workflow_patterns.merge.Merge
import pl.edu.agh.utils.ActorUtils.Implicits._
import pl.edu.agh.actions.ActionDsl._
import pl.edu.agh.dsl.WorkFlowDsl._

/** Prosty test sprawdzajacy, czy da się uzyc tego samego wezla wiecej niz jeden raz
  * oraz czy mozna zmienic akcje gdy uzyjemy wezla po raz drugi i czy mozna
  * zmienic wyjscie na ktore wysyla sie wynik
  * */
object ReusableTest extends App {

  val multiplyByTen = { (in: Int, outs: Outs) =>
    outs().foreach(out => in * 10 =>> out)
  }

  val mergeAct = { (in: Int, outs: Outs) =>
    in =>> outs("out1")
  }

  val act = { (in: Int, outs: Outs) =>
    outs().foreach(out => in =>> out)
  }

  val mergeProc = Merge[Int, Int] (
    name = "mergeProc",
    numOfOuts = 2,
    action = mergeAct
  )

  val splitProc = Merge[Int, Int] (
    name = "splitProc",
    numOfOuts = 3,
    action = act
  )

  val w = Workflow (
    "Reusable nodes test",
    numOfIns = 3,
    numOfOuts = 3,
    (ins: Seq[In[Int]], outs: Seq[Out[Int]]) => {
      ins(0) ~>> mergeProc
      ins(1) ~>> mergeProc
      ins(2) ~>> mergeProc

      splitProc changeActionOn multiplyByTen

      mergeProc.outs(1) ~> splitProc

      splitProc.outs(0) ~> mergeProc
      splitProc.outs(1) ~> mergeProc
      splitProc.outs(2) ~> mergeProc

      splitProc changeActionOn act

      mergeProc.outs(1) ~> splitProc

      splitProc.outs(0) ~>> outs(0)
      splitProc.outs(1) ~>> outs(1)
      splitProc.outs(2) ~>> outs(2)
    }
  )

  Source(1 to 4) ~> w.ins(0)
  Source(5 to 8) ~> w.ins(1)
  Source(9 to 12) ~> w.ins(2)

  val res = w.run
  println(res)
  println(w)

}
