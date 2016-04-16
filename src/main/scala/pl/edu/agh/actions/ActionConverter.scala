package pl.edu.agh.actions

/**
  * ActionConverter converts function into particular actions (defined in Actions.scala)
  */
object ActionConverter {
  def apply[T, R](action: T => R): ISingleAction[T, R] = Action[T, R](action)
}
