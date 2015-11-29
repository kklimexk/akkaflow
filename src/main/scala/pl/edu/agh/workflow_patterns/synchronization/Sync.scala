package pl.edu.agh.workflow_patterns.synchronization

import pl.edu.agh.utils.ActorUtils.system
import pl.edu.agh.actions.Action

//Sync
class Sync[T](action: Action[T]) {
  val syncActor = SyncActor(action)
}

object Sync {
  def apply[T](action: Action[T]) = new Sync[T](action).syncActor
}
