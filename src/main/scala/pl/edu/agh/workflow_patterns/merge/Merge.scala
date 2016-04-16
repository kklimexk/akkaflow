package pl.edu.agh.workflow_patterns.merge

import pl.edu.agh.actions.{ActionConverter, ISingleAction}
import pl.edu.agh.workflow_patterns.Pattern

//Merge
class Merge[T, R](name: String, numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R], sendTo: String) extends Pattern[T, R] {
  override lazy val actor = MergeActor(name, numOfOuts, outs, action, sendTo)
}

object Merge {
  def apply[T, R](name: String, numOfOuts: Int, action: T => R, sendTo: String) = new Merge[T, R](name, numOfOuts, Seq.empty, ActionConverter(action), sendTo)
  def apply[T, R](name: String, outs: Seq[String], action: T => R, sendTo: String) = new Merge[T, R](name, 0, outs, ActionConverter(action), sendTo)
}
