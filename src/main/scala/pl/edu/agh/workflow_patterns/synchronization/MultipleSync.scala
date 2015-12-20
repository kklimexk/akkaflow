package pl.edu.agh.workflow_patterns.synchronization

import pl.edu.agh.actions.MultipleAction

//Sync Pattern with multiple inputs
class MultipleSync[T](action: MultipleAction[T]) {
  lazy val syncActor = MultipleSyncActor(action)
}

object MultipleSync {
  def apply[T](action: MultipleAction[T]) = new MultipleSync[T](action)
}
