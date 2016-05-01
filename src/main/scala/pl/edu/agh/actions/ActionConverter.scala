package pl.edu.agh.actions

/**
  * ActionConverter converts function into particular actions (defined in Actions.scala)
  */
object ActionConverter {
  def apply[T, R](action: (T, Outs) => Unit): ISingleAction[T, R] = Action[T, R](action)
  def apply[T, R](action: T => Outs => Unit): ISingleAction[T, R] = Action2[T, R](action)
  def apply[T, R](action: (Ins[T], Outs) => Unit): IMultipleAction[T, R] = MultipleAction[T, R](action)
  def apply[T, R](action: Ins[T] => Outs => Unit): IMultipleAction[T, R] = MultipleAction2[T, R](action)
}
