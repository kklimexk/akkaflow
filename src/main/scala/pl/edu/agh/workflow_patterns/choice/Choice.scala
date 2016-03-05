package pl.edu.agh.workflow_patterns.choice

import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.workflow_patterns.Pattern

//Choice
class Choice[T, R](name: String, numOfOuts: Int, action: ISingleAction[T, R], choiceFunc: R => Int) extends Pattern[T, R] {
  override lazy val actor = ChoiceActor(name, numOfOuts, action, choiceFunc)
}

object Choice {
  def apply[T, R](name: String, numOfOuts: Int, action: ISingleAction[T, R], choiceFunc: R => Int) = new Choice(name, numOfOuts, action, choiceFunc)
}
