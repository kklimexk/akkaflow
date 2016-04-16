package pl.edu.agh.workflow_patterns.split

import pl.edu.agh.actions.{ActionConverter, ISingleAction}
import pl.edu.agh.workflow_patterns.Pattern

//Parallel Split
class Split[T, R](name: String, numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R]) extends Pattern[T, R] {
  override lazy val actor = SplitActor(name, numOfOuts, outs, action)
}

object Split {
  def apply[T, R](name: String, numOfOuts: Int, action: T => R) = new Split[T, R](name, numOfOuts, Seq.empty, ActionConverter(action))
  def apply[T, R](name: String, outs: Seq[String], action: T => R) = new Split[T, R](name, 0, outs, ActionConverter(action))
}
