package pl.edu.agh.workflow_patterns.synchronization

import java.util.concurrent.ConcurrentLinkedQueue

import pl.edu.agh.actions.{ActionConverter, IMultipleAction, INamedMultipleAction, IUnnamedMultipleAction}
import pl.edu.agh.workflow_patterns.Pattern

//Sync Pattern with multiple inputs
class Sync[T, R](name: String, numOfIns: Int, numOfOuts: Int, ins: Seq[String], outs: Seq[String], action: IMultipleAction[T, R], sendTo: String) extends Pattern[T, R] {

  //Ewentualnie mozna uzyc LinkedBlockingQueue
  val syncPointsQueues = {
    var res = Seq.empty[ConcurrentLinkedQueue[T]]
    var insCount: Int = 0
    action match {
      case _: IUnnamedMultipleAction[T, R] =>
        insCount = numOfIns
      case _: INamedMultipleAction[T, R] =>
        insCount = ins.size
    }
    for (i <- 0 until insCount) {
      res :+= new ConcurrentLinkedQueue[T]()
    }
    res
  }

  override lazy val actor = SyncActor(name, numOfOuts, ins, outs, action, sendTo, syncPointsQueues)
}

object Sync {
  def apply[T, R](name: String, numOfIns: Int, numOfOuts: Int, action: Seq[T] => R, sendTo: String) = new Sync[T, R](name, numOfIns, numOfOuts, Seq.empty, Seq.empty, ActionConverter(action), sendTo)
  def apply[T, R](name: String, numOfIns: Int, outs: Seq[String], action: Seq[T] => R, sendTo: String)(implicit d: DummyImplicit) = new Sync[T, R](name, numOfIns, 0, Seq.empty, outs, ActionConverter(action), sendTo)
  def apply[T, R](name: String, numOfOuts: Int, ins: Seq[String], action: Map[String, T] => R, sendTo: String) = new Sync[T, R](name, 0, numOfOuts, ins, Seq.empty, ActionConverter(action), sendTo)
  def apply[T, R](name: String, ins: Seq[String], outs: Seq[String], action: Map[String, T] => R, sendTo: String) = new Sync[T, R](name, 0, 0, ins, outs, ActionConverter(action), sendTo)
}
