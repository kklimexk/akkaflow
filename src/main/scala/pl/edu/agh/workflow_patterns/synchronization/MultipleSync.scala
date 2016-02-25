package pl.edu.agh.workflow_patterns.synchronization

import java.util.concurrent.ConcurrentLinkedQueue

import pl.edu.agh.actions.IMultipleAction
import pl.edu.agh.workflow_patterns.Pattern

//Sync Pattern with multiple inputs
class MultipleSync[T, K](name: String, numOfOuts: Int, action: IMultipleAction[T, K], sendTo: String) extends Pattern[T, K] {

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
  def apply[T, K](name: String, numOfOuts: Int, action: IMultipleAction[T, K], sendTo: String) = new MultipleSync[T, K](name, numOfOuts, action, sendTo)
}
