package pl.edu.agh.workflow_patterns.choice

import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.workflow_patterns.Pattern

//Choice
class Choice[T, K](name: String, numOfOuts: Int, action: ISingleAction[T, K], choiceFunc: K => Int) extends Pattern[T, K] {
  override lazy val actor = ChoiceActor(name, numOfOuts, action, choiceFunc)
}

object Choice {
  def apply[T, K](name: String, numOfOuts: Int, action: ISingleAction[T, K], choiceFunc: K => Int) = new Choice(name, numOfOuts, action, choiceFunc)
}
