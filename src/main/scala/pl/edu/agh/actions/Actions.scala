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

case class MultipleAction[T, K](action: Seq[T] => K) extends IMultipleAction[T, K] {

  /*TODO: Tutaj nie dziala ponizszy kod - nie wiadomo jak zcastowac reflect.runtime.universe.Type (head) na Seq[Int]
  if (action.isInstanceOf[Seq[Int] => Int]) {
    ReflectionUtils.getObjectArgs(action.asInstanceOf[Seq[Int] => Int]).head.asInstanceOf[Seq[Int]].size
  } else 0
   */
  var numOfIns: Int = ???

  def execute(ins: T*): K = {
    action(ins)
  }
}

//------------------------------------------------------------------------------------

sealed abstract class Acceptable[T]
object Acceptable {
  implicit object Int extends Acceptable[Int]
  implicit object ListOfInt extends Acceptable[List[Int]]
}
