package pl.edu.agh.actions

abstract class IAction[T: Acceptable] {
  var numOfIns: Int
  def execute(ins: T*): Int
}
abstract class ISingleAction[T: Acceptable] extends IAction[T]
abstract class IMultipleAction[T: Acceptable] extends IAction[T]

case class Action[T: Acceptable](action: T => Int) extends ISingleAction[T] {
  var numOfIns: Int = 1

  def execute(ins: T*): Int = {
    action(ins(0))
  }
}

case class Action2[T: Acceptable](action: (T, T) => Int) extends IMultipleAction[T] {
  var numOfIns: Int = 2

  def execute(ins: T*): Int = {
    action(ins(0), ins(1))
  }
}

case class Action3[T: Acceptable](action: (T, T, T) => Int) extends IMultipleAction[T] {
  var numOfIns: Int = 3

  def execute(ins: T*): Int = {
    action(ins(0), ins(1), ins(2))
  }
}

case class Action4[T: Acceptable](action: (T, T, T, T) => Int) extends IMultipleAction[T] {
  var numOfIns: Int = 4

  def execute(ins: T*): Int = {
    action(ins(0), ins(1), ins(2), ins(3))
  }
}

case class MultipleAction[T: Acceptable](action: Seq[T] => Int) extends IMultipleAction[T] {

  /*TODO: Tutaj nie dziala ponizszy kod - nie wiadomo jak zcastowac reflect.runtime.universe.Type (head) na Seq[Int]
  if (action.isInstanceOf[Seq[Int] => Int]) {
    ReflectionUtils.getObjectArgs(action.asInstanceOf[Seq[Int] => Int]).head.asInstanceOf[Seq[Int]].size
  } else 0
   */
  var numOfIns: Int = ???

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
