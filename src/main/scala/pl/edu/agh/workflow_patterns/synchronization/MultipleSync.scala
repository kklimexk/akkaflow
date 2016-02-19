package pl.edu.agh.workflow_patterns.synchronization

import java.util.concurrent.ConcurrentLinkedQueue

import pl.edu.agh.actions.IMultipleAction
import pl.edu.agh.workflow_patterns.Pattern

//Sync Pattern with multiple inputs
class MultipleSync[T, K](action: IMultipleAction[T, K]) extends Pattern[T, K] {

  //Ewentualnie mozna uzyc LinkedBlockingQueue
  val syncPointsQueues = {
    var res = Seq.empty[ConcurrentLinkedQueue[T]]
    for (i <- 0 until action.numOfIns) {
      res :+= new ConcurrentLinkedQueue[T]()
    }
    res
  }

  override lazy val actor = MultipleSyncActor(action, syncPointsQueues)
}

object MultipleSync {
  def apply[T, K](action: IMultipleAction[T, K]) = new MultipleSync[T, K](action)
}
