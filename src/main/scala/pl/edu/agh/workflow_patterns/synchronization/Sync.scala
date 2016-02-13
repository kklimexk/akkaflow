package pl.edu.agh.workflow_patterns.synchronization

import pl.edu.agh.actions.ISingleAction

//Sync
class Sync[T, K](action: ISingleAction[T, K]) {
  lazy val syncActor = SyncActor(action)
}

object Sync {
  def apply[T, K](action: ISingleAction[T, K]) = new Sync[T, K](action)
}
