package pl.edu.agh.workflow_patterns.synchronization

import pl.edu.agh.actions.Action2

//Sync Pattern with multiple inputs
class MultipleSync[T](action: Action2[T]) {
  lazy val syncActor = MultipleSyncActor(action)
}

object MultipleSync {
  def apply[T](action: Action2[T]) = new MultipleSync[T](action)
}
