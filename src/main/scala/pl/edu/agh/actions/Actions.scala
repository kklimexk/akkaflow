package pl.edu.agh.actions

abstract class IAction[T, R] {
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

case class Action2[T, R](action: (T, T) => R) extends IMultipleAction[T, R] {
  var numOfIns: Int = 2

  def execute(ins: T*): R = {
    action(ins(0), ins(1))
  }
}

case class Action3[T, R](action: (T, T, T) => R) extends IMultipleAction[T, R] {
  var numOfIns: Int = 3

  def execute(ins: T*): R = {
    action(ins(0), ins(1), ins(2))
  }
}

case class Action4[T, R](action: (T, T, T, T) => R) extends IMultipleAction[T, R] {
  var numOfIns: Int = 4

  def execute(ins: T*): R = {
    action(ins(0), ins(1), ins(2), ins(3))
  }
}

case class Action5[T, R](action: (T, T, T, T, T) => R) extends IMultipleAction[T, R] {
  var numOfIns: Int = 5

  def execute(ins: T*): R = {
    action(ins(0), ins(1), ins(2), ins(3), ins(4))
  }
}

case class Action6[T, R](action: (T, T, T, T, T, T) => R) extends IMultipleAction[T, R] {
  var numOfIns: Int = 6

  def execute(ins: T*): R = {
    action(ins(0), ins(1), ins(2), ins(3), ins(4), ins(5))
  }
}

case class Action7[T, R](action: (T, T, T, T, T, T, T) => R) extends IMultipleAction[T, R] {
  var numOfIns: Int = 7

  def execute(ins: T*): R = {
    action(ins(0), ins(1), ins(2), ins(3), ins(4), ins(5), ins(6))
  }
}

case class Action8[T, R](action: (T, T, T, T, T, T, T, T) => R) extends IMultipleAction[T, R] {
  var numOfIns: Int = 8

  def execute(ins: T*): R = {
    action(ins(0), ins(1), ins(2), ins(3), ins(4), ins(5), ins(6), ins(7))
  }
}

case class MultipleAction[T, R](var numOfIns: Int)(action: Seq[T] => R) extends IMultipleAction[T, R] {
  def execute(ins: T*): R = {
    assert(numOfIns == ins.size, "NumOfIns must equals to ins.size!")
    action(ins)
  }
}

//------------------------------------------------------------------------------------

sealed abstract class Acceptable[T]
object Acceptable {
  implicit object Int extends Acceptable[Int]
  implicit object ListOfInt extends Acceptable[List[Int]]
}
