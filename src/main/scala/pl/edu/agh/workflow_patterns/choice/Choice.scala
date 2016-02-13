package pl.edu.agh.workflow_patterns.choice

import pl.edu.agh.actions.ISingleAction

//Choice
class Choice[T, K](action: ISingleAction[T, K], conditions: K => (Boolean, Boolean, Boolean)) {
  lazy val choiceActor = ChoiceActor(action, conditions)
}

object Choice {
  def apply[T, K](action: ISingleAction[T, K], conditions: K => (Boolean, Boolean, Boolean)) = new Choice(action, conditions)
}
