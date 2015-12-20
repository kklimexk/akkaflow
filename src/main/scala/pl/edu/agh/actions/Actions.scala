package pl.edu.agh.actions

abstract class IAction[T: Acceptable]

case class Action[T: Acceptable](action: T => Int) extends IAction[T] {
  def execute(in: T): Int = {
    action(in)
  }
}

case class MultipleAction[T: Acceptable](action: (T, T) => Int) extends IAction[T] {
  def execute(in1: T, in2: T): Int = {
    action(in1, in2)
  }
}

//------------------------------------------------------------------------------------

sealed abstract class Acceptable[T]
object Acceptable {
  implicit object Int extends Acceptable[Int]
  implicit object ListOfInt extends Acceptable[List[Int]]
}
