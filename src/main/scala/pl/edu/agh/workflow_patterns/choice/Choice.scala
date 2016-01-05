package pl.edu.agh.workflow_patterns.choice

import pl.edu.agh.actions.ISingleAction

//Choice
class Choice[T](action: ISingleAction[T], conditions: Int => (Boolean, Boolean, Boolean)) {
  lazy val choiceActor = ChoiceActor(action, conditions)
}

object Choice {
  def apply[T](action: ISingleAction[T], conditions: Int => (Boolean, Boolean, Boolean)) = new Choice(action, conditions)
}
