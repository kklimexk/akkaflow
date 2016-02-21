package pl.edu.agh.workflow_patterns.choice

import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.workflow_patterns.Pattern

//Choice
class Choice[T, K](numOfOuts: Int, action: ISingleAction[T, K], choiceFunc: K => Int) extends Pattern[T, K] {
  override lazy val actor = ChoiceActor(numOfOuts, action, choiceFunc)
}

object Choice {
  def apply[T, K](numOfOuts: Int, action: ISingleAction[T, K], choiceFunc: K => Int) = new Choice(numOfOuts, action, choiceFunc)
}
