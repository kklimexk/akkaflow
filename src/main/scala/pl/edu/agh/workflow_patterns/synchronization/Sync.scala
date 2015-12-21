package pl.edu.agh.workflow_patterns.synchronization

import pl.edu.agh.actions.ISingleAction

//Sync
class Sync[T](action: ISingleAction[T]) {
  lazy val syncActor = SyncActor(action)
}

object Sync {
  def apply[T](action: ISingleAction[T]) = new Sync[T](action)
}
