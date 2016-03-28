package pl.edu.agh.workflow_patterns.choice

import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.workflow_patterns.Pattern

//Choice
class Choice[T, R](name: String, numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R], choiceFunc: R => Int) extends Pattern[T, R] {
  override lazy val actor = ChoiceActor(name, numOfOuts, outs, action, choiceFunc)
}

object Choice {
  def apply[T, R](name: String, numOfOuts: Int, action: ISingleAction[T, R], choiceFunc: R => Int) = new Choice(name, numOfOuts, Seq.empty, action, choiceFunc)
  def apply[T, R](name: String, outs: Seq[String], action: ISingleAction[T, R], choiceFunc: R => Int) = new Choice(name, 0, outs, action, choiceFunc)
}
