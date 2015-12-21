package pl.edu.agh.workflow_patterns.synchronization

import pl.edu.agh.actions.IMultipleAction

//Sync Pattern with multiple inputs
class MultipleSync[T](action: IMultipleAction[T]) {
  lazy val syncActor = MultipleSyncActor(action)
}

object MultipleSync {
  def apply[T](action: IMultipleAction[T]) = new MultipleSync[T](action)
}
