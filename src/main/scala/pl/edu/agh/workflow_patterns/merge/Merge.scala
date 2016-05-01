package pl.edu.agh.workflow_patterns.merge

import pl.edu.agh.actions.{ActionConverter, ISingleAction, Outs}
import pl.edu.agh.workflow_patterns.Pattern

//Merge
class Merge[T, R](name: String, numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R]) extends Pattern[T, R] {
  override lazy val actor = MergeActor(name, numOfOuts, outs, action)
}

object Merge {
  def apply[T, R](name: String, numOfOuts: Int, action: (T, Outs) => Unit) = new Merge[T, R](name, numOfOuts, Seq.empty, ActionConverter[T, R](action))
  def apply[T, R](name: String, outs: Seq[String], action: (T, Outs) => Unit) = new Merge[T, R](name, 0, outs, ActionConverter[T, R](action))
  def apply[T, R](name: String, numOfOuts: Int, action: T => Outs => Unit) = new Merge[T, R](name, numOfOuts, Seq.empty, ActionConverter[T, R](action))
  def apply[T, R](name: String, outs: Seq[String], action: T => Outs => Unit) = new Merge[T, R](name, 0, outs, ActionConverter[T, R](action))
}
