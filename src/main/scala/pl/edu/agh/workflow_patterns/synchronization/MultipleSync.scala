package pl.edu.agh.workflow_patterns.synchronization

import java.util.concurrent.ConcurrentLinkedQueue

import pl.edu.agh.actions.IMultipleAction
import pl.edu.agh.workflow_patterns.Pattern

//Sync Pattern with multiple inputs
class MultipleSync[T, R](name: String, numOfOuts: Int, action: IMultipleAction[T, R], sendTo: String) extends Pattern[T, R] {

  //Ewentualnie mozna uzyc LinkedBlockingQueue
  val syncPointsQueues = {
    var res = Seq.empty[ConcurrentLinkedQueue[T]]
    for (i <- 0 until action.numOfIns) {
      res :+= new ConcurrentLinkedQueue[T]()
    }
    res
  }

  override lazy val actor = MultipleSyncActor(name, numOfOuts, action, sendTo, syncPointsQueues)
}

object MultipleSync {
  def apply[T, R](name: String, numOfOuts: Int, action: IMultipleAction[T, R], sendTo: String) = new MultipleSync[T, R](name, numOfOuts, action, sendTo)
}
