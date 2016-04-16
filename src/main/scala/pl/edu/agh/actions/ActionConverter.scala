package pl.edu.agh.actions

/**
  * ActionConverter converts function into particular actions (defined in Actions.scala)
  */
object ActionConverter {
  def apply[T, R](action: T => R): ISingleAction[T, R] = Action[T, R](action)
  def apply[T, R](action: Seq[T] => R): IUnnamedMultipleAction[T, R] = MultipleAction[T, R](action)
  def apply[T, R](action: Map[String, T] => R): INamedMultipleAction[T, R] = NamedMultipleAction[T, R](action)
}
