package pl.edu.agh.workflow_patterns.synchronization

import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.workflow_patterns.Pattern

//Sync
class Sync[T, K](action: ISingleAction[T, K]) extends Pattern[T, K] {
  override lazy val actor = SyncActor(action)
}

object Sync {
  def apply[T, K](action: ISingleAction[T, K]) = new Sync[T, K](action)
}
