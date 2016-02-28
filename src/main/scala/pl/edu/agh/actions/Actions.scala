package pl.edu.agh.actions

abstract class IAction[T, K] {
  var numOfIns: Int
  def execute(ins: T*): K
}
abstract class ISingleAction[T, K] extends IAction[T, K]
abstract class IMultipleAction[T, K] extends IAction[T, K]

case class Action[T, K](action: T => K) extends ISingleAction[T, K] {
  var numOfIns: Int = 1

  def execute(ins: T*): K = {
    action(ins(0))
  }
}

case class Action2[T, K](action: (T, T) => K) extends IMultipleAction[T, K] {
  var numOfIns: Int = 2

  def execute(ins: T*): K = {
    action(ins(0), ins(1))
  }
}

case class Action3[T, K](action: (T, T, T) => K) extends IMultipleAction[T, K] {
  var numOfIns: Int = 3

  def execute(ins: T*): K = {
    action(ins(0), ins(1), ins(2))
  }
}

case class Action4[T, K](action: (T, T, T, T) => K) extends IMultipleAction[T, K] {
  var numOfIns: Int = 4

  def execute(ins: T*): K = {
    action(ins(0), ins(1), ins(2), ins(3))
  }
}

case class Action5[T, K](action: (T, T, T, T, T) => K) extends IMultipleAction[T, K] {
  var numOfIns: Int = 5

  def execute(ins: T*): K = {
    action(ins(0), ins(1), ins(2), ins(3), ins(4))
  }
}

case class Action6[T, K](action: (T, T, T, T, T, T) => K) extends IMultipleAction[T, K] {
  var numOfIns: Int = 6

  def execute(ins: T*): K = {
    action(ins(0), ins(1), ins(2), ins(3), ins(4), ins(5))
  }
}

case class Action7[T, K](action: (T, T, T, T, T, T, T) => K) extends IMultipleAction[T, K] {
  var numOfIns: Int = 7

  def execute(ins: T*): K = {
    action(ins(0), ins(1), ins(2), ins(3), ins(4), ins(5), ins(6))
  }
}

case class Action8[T, K](action: (T, T, T, T, T, T, T, T) => K) extends IMultipleAction[T, K] {
  var numOfIns: Int = 8

  def execute(ins: T*): K = {
    action(ins(0), ins(1), ins(2), ins(3), ins(4), ins(5), ins(6), ins(7))
  }
}

case class MultipleAction[T, K](var numOfIns: Int)(action: Seq[T] => K) extends IMultipleAction[T, K] {
  def execute(ins: T*): K = {
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
