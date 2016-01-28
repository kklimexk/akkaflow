package pl.edu.agh.workflow_patterns.synchronization

import java.util.concurrent.ConcurrentLinkedQueue

import pl.edu.agh.actions.IMultipleAction

//Sync Pattern with multiple inputs
class MultipleSync[T](action: IMultipleAction[T]) {

  //Ewentualnie mozna uzyc LinkedBlockingQueue
  val syncPointsQueues = {
    var res = Seq.empty[ConcurrentLinkedQueue[T]]
    for (i <- 0 until action.numOfIns) {
      res :+= new ConcurrentLinkedQueue[T]()
    }
    res
  }

  lazy val syncActor = MultipleSyncActor(action, syncPointsQueues)
}

object MultipleSync {
  def apply[T](action: IMultipleAction[T]) = new MultipleSync[T](action)
}
