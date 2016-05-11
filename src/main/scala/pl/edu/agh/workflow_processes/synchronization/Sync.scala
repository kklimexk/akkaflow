package pl.edu.agh.workflow_processes.synchronization

import java.util.concurrent.ConcurrentLinkedQueue

import pl.edu.agh.actions._
import pl.edu.agh.workflow_processes.Pattern

/**
  * Synchronization process (input elements are synchronized)
  */
class Sync[T, R](var _name: String,
                 var _numOfIns: Int,
                 var _numOfOuts: Int,
                 var _ins: Seq[String],
                 var _outs: Seq[String],
                 var _action: IMultipleAction[T, R]) extends Pattern[T, R] {

  def this() {
    this("", 0, 0, Seq.empty[String], Seq.empty[String], EmptyMultipleAction[T, R]())
  }

  //Ewentualnie mozna uzyc LinkedBlockingQueue
  lazy val syncPointsQueues = {
    var res = Seq.empty[ConcurrentLinkedQueue[T]]
    var insCount: Int = 0
    if (_numOfIns > 0) {
      insCount = _numOfIns
    } else if (_ins.nonEmpty) {
      insCount = _ins.size
    }
    for (i <- 0 until insCount) {
      res :+= new ConcurrentLinkedQueue[T]()
    }
    res
  }

  override lazy val actor = SyncActor(_name, _numOfOuts, _ins, _outs, _action, syncPointsQueues)
}

object Sync {
  def apply[T, R](name: String, numOfIns: Int, numOfOuts: Int, action: (Ins[T], Outs) => Unit) = new Sync[T, R](name, numOfIns, numOfOuts, Seq.empty, Seq.empty, ActionConverter[T, R](action))
  def apply[T, R](name: String, numOfIns: Int, outs: Seq[String], action: (Ins[T], Outs) => Unit)(implicit d: DummyImplicit) = new Sync[T, R](name, numOfIns, 0, Seq.empty, outs, ActionConverter[T, R](action))
  def apply[T, R](name: String, ins: Seq[String], numOfOuts: Int, action: (Ins[T], Outs) => Unit) = new Sync[T, R](name, 0, numOfOuts, ins, Seq.empty, ActionConverter[T, R](action))
  def apply[T, R](name: String, ins: Seq[String], outs: Seq[String], action: (Ins[T], Outs) => Unit) = new Sync[T, R](name, 0, 0, ins, outs, ActionConverter[T, R](action))
  def apply[T, R](name: String, numOfIns: Int, numOfOuts: Int, action: Ins[T] => Outs => Unit) = new Sync[T, R](name, numOfIns, numOfOuts, Seq.empty, Seq.empty, ActionConverter[T, R](action))
  def apply[T, R](name: String, numOfIns: Int, outs: Seq[String], action: Ins[T] => Outs => Unit)(implicit d: DummyImplicit) = new Sync[T, R](name, numOfIns, 0, Seq.empty, outs, ActionConverter[T, R](action))
  def apply[T, R](name: String, ins: Seq[String], numOfOuts: Int, action: Ins[T] => Outs => Unit) = new Sync[T, R](name, 0, numOfOuts, ins, Seq.empty, ActionConverter[T, R](action))
  def apply[T, R](name: String, ins: Seq[String], outs: Seq[String], action: Ins[T] => Outs => Unit) = new Sync[T, R](name, 0, 0, ins, outs, ActionConverter[T, R](action))
  def apply[T, R] = new Sync[T, R]
}
