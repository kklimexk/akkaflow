package pl.edu.agh.actions

trait Transformation

abstract class IAction[T, R] extends Transformation
//-----------------------------------------------------------
abstract class ISingleAction[T, R] extends IAction[T, R] {
  def execute(ins: T): R
}
abstract class IMultipleAction[T, R] extends IAction[T, R] {
  def execute(ins: T*): R = throw new NotImplementedError("Use this method for MultipleAction class only!")
  def execute(ins: Map[String, T]): R = throw new NotImplementedError("Use this method for NamedMultipleAction class only!")
}
abstract class IUnnamedMultipleAction[T, R] extends IMultipleAction[T, R] {
  var numOfIns: Int
  def execute(ins: T*): R
}
abstract class INamedMultipleAction[T, R] extends IMultipleAction[T, R] {
  def execute(ins: Map[String, T]): R
}
//------------------------------------------------------------
case class Action[T, R](action: T => R) extends ISingleAction[T, R] {
  def execute(in: T): R = {
    action(in)
  }
}

case class MultipleAction[T, R](var numOfIns: Int)(action: Seq[T] => R) extends IUnnamedMultipleAction[T, R] {
  override def execute(ins: T*): R = {
    assert(numOfIns == ins.size, "NumOfIns must equals to ins.size!")
    action(ins)
  }
}

case class NamedMultipleAction[T, R](action: Map[String, T] => R) extends INamedMultipleAction[T, R] {
  override def execute(ins: Map[String, T]): R = {
    action(ins)
  }
}
