package pl.edu.agh.actions

abstract class IAction[T: Acceptable] {
  def execute(ins: T*): Int
}

case class Action[T: Acceptable](action: T => Int) extends IAction[T] {
  def execute(ins: T*): Int = {
    action(ins(0))
  }
}

case class Action2[T: Acceptable](action: (T, T) => Int) extends IAction[T] {
  def execute(ins: T*): Int = {
    action(ins(0), ins(1))
  }
}

case class Action3[T: Acceptable](action: (T, T, T) => Int) extends IAction[T] {
  def execute(ins: T*): Int = {
    action(ins(0), ins(1), ins(2))
  }
}

case class MultipleAction[T: Acceptable](action: Seq[T] => Int) extends IAction[T] {
  def execute(ins: T*): Int = {
    action(ins)
  }
}

//------------------------------------------------------------------------------------

sealed abstract class Acceptable[T]
object Acceptable {
  implicit object Int extends Acceptable[Int]
  implicit object ListOfInt extends Acceptable[List[Int]]
}
