package pl.edu.agh.workflow_patterns.merge

import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.workflow_patterns.Pattern

//Merge
class Merge[T, K](name: String, numOfOuts: Int, action: ISingleAction[T, K], sendTo: String) extends Pattern[T, K] {
  override lazy val actor = MergeActor(name, numOfOuts, action, sendTo)
}

object Merge {
  def apply[T, K](name: String, numOfOuts: Int, action: ISingleAction[T, K], sendTo: String) = new Merge[T, K](name, numOfOuts, action, sendTo)
}
