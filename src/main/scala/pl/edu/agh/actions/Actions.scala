package pl.edu.agh.actions

trait Transformation

abstract class IAction[T, R] extends Transformation {
  var numOfIns: Int
  def execute(ins: T*): R
}
abstract class ISingleAction[T, R] extends IAction[T, R]
abstract class IMultipleAction[T, R] extends IAction[T, R]

case class Action[T, R](action: T => R) extends ISingleAction[T, R] {
  var numOfIns: Int = 1

  def execute(ins: T*): R = {
    action(ins(0))
  }
}

case class MultipleAction[T, R](var numOfIns: Int)(action: Seq[T] => R) extends IMultipleAction[T, R] {
  def execute(ins: T*): R = {
    assert(numOfIns == ins.size, "NumOfIns must equals to ins.size!")
    action(ins)
  }
}
