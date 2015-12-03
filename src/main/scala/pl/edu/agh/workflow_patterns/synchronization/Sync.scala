package pl.edu.agh.workflow_patterns.synchronization

import pl.edu.agh.actions.Action

//Sync
class Sync[T](action: Action[T]) {
  lazy val syncActor = SyncActor(action)
}

object Sync {
  def apply[T](action: Action[T]) = new Sync[T](action)
}
