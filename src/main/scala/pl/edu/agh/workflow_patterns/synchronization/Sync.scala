package pl.edu.agh.workflow_patterns.synchronization

import java.util.concurrent.ConcurrentLinkedQueue

import pl.edu.agh.actions._
import pl.edu.agh.workflow_patterns.Pattern

//Sync Pattern with multiple inputs
class Sync[T, R](name: String, numOfIns: Int, numOfOuts: Int, ins: Seq[String], outs: Seq[String], action: IMultipleAction[T, R]) extends Pattern[T, R] {

  //Ewentualnie mozna uzyc LinkedBlockingQueue
  val syncPointsQueues = {
    var res = Seq.empty[ConcurrentLinkedQueue[T]]
    var insCount: Int = 0
    if (numOfIns > 0) {
      insCount = numOfIns
    } else if (ins.nonEmpty) {
      insCount = ins.size
    }
    for (i <- 0 until insCount) {
      res :+= new ConcurrentLinkedQueue[T]()
    }
    res
  }

  override lazy val actor = SyncActor(name, numOfOuts, ins, outs, action, syncPointsQueues)
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
}
