package pl.edu.agh.workflow_patterns.synchronization

import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.workflow_patterns.Pattern

//Sync
class Sync[T, R](name: String, numOfOuts: Int, action: ISingleAction[T, R], sendTo: String) extends Pattern[T, R] {
  override lazy val actor = SyncActor(name, numOfOuts, action, sendTo)
}

object Sync {
  def apply[T, R](name: String, numOfOuts: Int, action: ISingleAction[T, R], sendTo: String) = new Sync[T, R](name, numOfOuts, action, sendTo)
}
