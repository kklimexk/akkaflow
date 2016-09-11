package pl.edu.agh.workflow_processes.discriminator

import java.util.concurrent.ConcurrentLinkedQueue

import akka.actor.ActorRef
import pl.edu.agh.actions._
import pl.edu.agh.workflow_processes.Pattern

class Disc[T, R](var _name: String,
                 var _n: Int,
                 var _m: Int,
                 var _numOfOuts: Int,
                 var _ins: Seq[String],
                 var _outs: Seq[String],
                 var _action: IMultipleAction[T, R]) extends Pattern[T, R] {

  def this() {
    this("", 0, 0, 0, Seq.empty[String], Seq.empty[String], EmptyMultipleAction[T, R]())
  }

  lazy val syncPointsQueues = {
    var res = Seq.empty[ConcurrentLinkedQueue[T]]
    var insCount: Int = 0
    if (_m > 0) {
      insCount = _m
    } else if (_ins.nonEmpty) {
      insCount = _ins.size
    }
    for (i <- 0 until insCount) {
      res :+= new ConcurrentLinkedQueue[T]()
    }
    res
  }

  override lazy val actor: ActorRef = DiscActor(_name, _numOfOuts, _ins, _n, _outs, _action, syncPointsQueues)
}

object Disc {
  def apply[T, R](name: String, n: Int, m: Int, numOfOuts: Int, action: (Ins[T], Outs) => Unit) = new Disc[T, R](name, n, m, numOfOuts, Seq.empty, Seq.empty, ActionConverter[T, R](action))
  def apply[T, R](name: String, n: Int, m: Int, outs: Seq[String], action: (Ins[T], Outs) => Unit)(implicit d: DummyImplicit) = new Disc[T, R](name, n, m, 0, Seq.empty, outs, ActionConverter[T, R](action))
  def apply[T, R](name: String, n: Int, ins: Seq[String], numOfOuts: Int, action: (Ins[T], Outs) => Unit) = new Disc[T, R](name, n, 0, numOfOuts, ins, Seq.empty, ActionConverter[T, R](action))
  def apply[T, R](name: String, n: Int, ins: Seq[String], outs: Seq[String], action: (Ins[T], Outs) => Unit) = new Disc[T, R](name, n, 0, 0, ins, outs, ActionConverter[T, R](action))
  def apply[T, R](name: String, n: Int, m: Int, numOfOuts: Int, action: Ins[T] => Outs => Unit) = new Disc[T, R](name, n, m, numOfOuts, Seq.empty, Seq.empty, ActionConverter[T, R](action))
  def apply[T, R](name: String, n: Int, m: Int, outs: Seq[String], action: Ins[T] => Outs => Unit)(implicit d: DummyImplicit) = new Disc[T, R](name, n, m, 0, Seq.empty, outs, ActionConverter[T, R](action))
  def apply[T, R](name: String, n: Int, ins: Seq[String], numOfOuts: Int, action: Ins[T] => Outs => Unit) = new Disc[T, R](name, n, 0, numOfOuts, ins, Seq.empty, ActionConverter[T, R](action))
  def apply[T, R](name: String, n: Int, ins: Seq[String], outs: Seq[String], action: Ins[T] => Outs => Unit) = new Disc[T, R](name, n, 0, 0, ins, outs, ActionConverter[T, R](action))
  def apply[T, R] = new Disc[T, R]
}
