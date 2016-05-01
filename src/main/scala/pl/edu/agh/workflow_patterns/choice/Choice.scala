package pl.edu.agh.workflow_patterns.choice

import pl.edu.agh.actions.{ActionConverter, ISingleAction, Outs}
import pl.edu.agh.workflow_patterns.Pattern

//Choice
@deprecated(message = "There is no need to use it!")
class Choice[T, R](name: String, numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R]) extends Pattern[T, R] {
  override lazy val actor = ChoiceActor(name, numOfOuts, outs, action)
}

@deprecated(message = "There is no need to use it!")
object Choice {
  @deprecated(message = "There is no need to use it!")
  def apply[T, R](name: String, numOfOuts: Int, action: (T, Outs) => Unit) = new Choice(name, numOfOuts, Seq.empty, ActionConverter[T, R](action))
  @deprecated(message = "There is no need to use it!")
  def apply[T, R](name: String, outs: Seq[String], action: (T, Outs) => Unit) = new Choice(name, 0, outs, ActionConverter[T, R](action))
}
