package pl.edu.agh.workflow_patterns.split

import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.workflow_patterns.Pattern

//Parallel Split
class Split[T, R](name: String, numOfOuts: Int, action: ISingleAction[T, R]) extends Pattern[T, R] {
  override lazy val actor = SplitActor(name, numOfOuts, action)
}

object Split {
  def apply[T, R](name: String, numOfOuts: Int, action: ISingleAction[T, R]) = new Split[T, R](name, numOfOuts, action)
}
