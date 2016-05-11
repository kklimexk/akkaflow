package pl.edu.agh.actions

import akka.actor.ActorRef
import pl.edu.agh.messages.ResultMessage

trait Transformation

abstract class IAction[T, R] extends Transformation
//-----------------------------------------------------------
abstract class ISingleAction[T, R] extends IAction[T, R] {
  def execute(ins: T)(outs: Outs): Unit
}
abstract class IMultipleAction[T, R] extends IAction[T, R] {
  def execute(ins: Ins[T])(outs: Outs): Unit
}
//-----------------------------------------------------------
case class EmptySingleAction[T, R]() extends ISingleAction[T, R] {
  override def execute(ins: T)(outs: Outs): Unit = ()
}
//------------------------------------------------------------
case class Action[T, R](action: (T, Outs) => Unit) extends ISingleAction[T, R] {
  def execute(in: T)(outs: Outs): Unit = {
    action(in, outs)
  }
}

case class Action2[T, R](action: T => Outs => Unit) extends ISingleAction[T, R] {
  def execute(in: T)(outs: Outs): Unit = {
    action(in)(outs)
  }
}

case class MultipleAction[T, R](action: (Ins[T], Outs) => Unit) extends IMultipleAction[T, R] {
  override def execute(ins: Ins[T])(outs: Outs): Unit = {
    action(ins, outs)
  }
}

case class MultipleAction2[T, R](action: Ins[T] => Outs => Unit) extends IMultipleAction[T, R] {
  override def execute(ins: Ins[T])(outs: Outs): Unit = {
    action(ins)(outs)
  }
}
//------------------------------------------------------------
object ActionDsl {
  implicit class SendResultToOutput[R](result: R) {
    def =>>(out: ActorRef) = {
      out ! ResultMessage(result)
    }
    def =>>(out: (String, ActorRef)) = {
      out._2 ! ResultMessage(result)
    }
    def =>>(outName: String)(implicit outs: Outs) = {
      outs(outName) ! ResultMessage(result)
    }
  }
}
