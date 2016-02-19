package pl.edu.agh.workflow_patterns.merge

import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.workflow_patterns.Pattern

//Merge
class Merge[T, K](action: ISingleAction[T, K]) extends Pattern[T, K] {
  override lazy val actor = MergeActor(action)
}

object Merge {
  def apply[T, K](action: ISingleAction[T, K]) = new Merge[T, K](action)
}
