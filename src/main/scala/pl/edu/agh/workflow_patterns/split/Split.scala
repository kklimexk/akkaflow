package pl.edu.agh.workflow_patterns.split

import pl.edu.agh.actions.{ActionConverter, ISingleAction, Outs}
import pl.edu.agh.workflow_patterns.Pattern

//Parallel Split
@deprecated(message = "There is no need to use it!")
class Split[T, R](name: String, numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R]) extends Pattern[T, R] {
  override lazy val actor = SplitActor(name, numOfOuts, outs, action)
}

@deprecated(message = "There is no need to use it!")
object Split {
  @deprecated(message = "There is no need to use it!")
  def apply[T, R](name: String, numOfOuts: Int, action: (T, Outs) => Unit) = new Split[T, R](name, numOfOuts, Seq.empty, ActionConverter[T, R](action))
  @deprecated(message = "There is no need to use it!")
  def apply[T, R](name: String, outs: Seq[String], action: (T, Outs) => Unit) = new Split[T, R](name, 0, outs, ActionConverter[T, R](action))
}
