package pl.edu.agh.workflow_patterns.synchronization

import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.workflow_patterns.Pattern

//Sync
class Sync[T, K](name: String, numOfOuts: Int, action: ISingleAction[T, K], sendTo: String) extends Pattern[T, K] {
  override lazy val actor = SyncActor(name, numOfOuts, action, sendTo)
}

object Sync {
  def apply[T, K](name: String, numOfOuts: Int, action: ISingleAction[T, K], sendTo: String) = new Sync[T, K](name, numOfOuts, action, sendTo)
}
