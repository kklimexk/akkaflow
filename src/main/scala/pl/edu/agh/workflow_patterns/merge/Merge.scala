package pl.edu.agh.workflow_patterns.merge

import pl.edu.agh.actions.ISingleAction

//Merge
class Merge[T, K](action: ISingleAction[T, K]) {
  lazy val mergeActor = MergeActor(action)
}

object Merge {
  def apply[T, K](action: ISingleAction[T, K]) = new Merge[T, K](action)
}
