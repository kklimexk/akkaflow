package pl.edu.agh.actions

/**
  * ActionConverter converts function into particular actions (defined in Actions.scala)
  */
object ActionConverter {
  def apply[T, R](action: (T, Outs) => Unit): ISingleAction[T, R] = Action[T, R](action)
  def apply[T, R](action: (Seq[T], Outs) => Unit): IUnnamedMultipleAction[T, R] = MultipleAction[T, R](action)
  def apply[T, R](action: (Map[String, T], Outs) => Unit): INamedMultipleAction[T, R] = NamedMultipleAction[T, R](action)
}
