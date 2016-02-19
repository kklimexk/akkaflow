package pl.edu.agh.workflow_patterns.choice

import pl.edu.agh.actions.ISingleAction

//Choice
class Choice[T, K](numOfIns: Int, numOfOuts: Int, action: ISingleAction[T, K], choiceFunc: K => Int) {
  lazy val choiceActor = ChoiceActor(numOfIns, numOfOuts, action, choiceFunc)
}

object Choice {
  def apply[T, K](numOfIns: Int, numOfOuts: Int, action: ISingleAction[T, K], choiceFunc: K => Int) = new Choice(numOfIns, numOfOuts, action, choiceFunc)
}
