package pl.edu.agh.workflow_patterns.synchronization

import java.util.concurrent.ConcurrentLinkedQueue

import pl.edu.agh.actions.IMultipleAction
import pl.edu.agh.workflow_patterns.Pattern

//Sync Pattern with multiple inputs
class Sync[T, R](name: String, numOfOuts: Int, ins: Seq[String], outs: Seq[String], action: IMultipleAction[T, R], sendTo: String) extends Pattern[T, R] {

  //Ewentualnie mozna uzyc LinkedBlockingQueue
  val syncPointsQueues = {
    var res = Seq.empty[ConcurrentLinkedQueue[T]]
    for (i <- 0 until action.numOfIns) {
      res :+= new ConcurrentLinkedQueue[T]()
    }
    res
  }

  override lazy val actor = SyncActor(name, numOfOuts, ins, outs, action, sendTo, syncPointsQueues)
}

object Sync {
  def apply[T, R](name: String, numOfOuts: Int, action: IMultipleAction[T, R], sendTo: String) = new Sync[T, R](name, numOfOuts, Seq.empty, Seq.empty, action, sendTo)
  def apply[T, R](name: String, outs: Seq[String], action: IMultipleAction[T, R], sendTo: String) = new Sync[T, R](name, 0, Seq.empty, outs, action, sendTo)

  def apply[T, R](name: String, numOfOuts: Int, ins: Seq[String], action: IMultipleAction[T, R], sendTo: String) = new Sync[T, R](name, numOfOuts, ins, Seq.empty, action, sendTo)
  def apply[T, R](name: String, ins: Seq[String], outs: Seq[String], action: IMultipleAction[T, R], sendTo: String) = new Sync[T, R](name, 0, ins, outs, action, sendTo)
}
