package pl.edu.agh.workflow_patterns.merge

import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.workflow_patterns.Pattern

//Merge
class Merge[T, R](name: String, numOfOuts: Int, action: ISingleAction[T, R], sendTo: String) extends Pattern[T, R] {
  override lazy val actor = MergeActor(name, numOfOuts, action, sendTo)
}

object Merge {
  def apply[T, R](name: String, numOfOuts: Int, action: ISingleAction[T, R], sendTo: String) = new Merge[T, R](name, numOfOuts, action, sendTo)
}
