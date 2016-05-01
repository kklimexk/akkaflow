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
  def execute(ins: T*)(outs: Outs): Unit = throw new NotImplementedError("Use this method for MultipleAction class only!")
  def execute(ins: Map[String, T])(outs: Outs): Unit = throw new NotImplementedError("Use this method for NamedMultipleAction class only!")
}
abstract class IUnnamedMultipleAction[T, R] extends IMultipleAction[T, R] {
  def execute(ins: T*)(outs: Outs): Unit
}
abstract class INamedMultipleAction[T, R] extends IMultipleAction[T, R] {
  def execute(ins: Map[String, T])(outs: Outs): Unit
}
//------------------------------------------------------------
case class Action[T, R](action: (T, Outs) => Unit) extends ISingleAction[T, R] {
  def execute(in: T)(outs: Outs): Unit = {
    action(in, outs)
  }
}

case class MultipleAction[T, R](action: (Seq[T], Outs) => Unit) extends IUnnamedMultipleAction[T, R] {
  override def execute(ins: T*)(outs: Outs): Unit = {
    action(ins, outs)
  }
}

case class NamedMultipleAction[T, R](action: (Map[String, T], Outs) => Unit) extends INamedMultipleAction[T, R] {
  override def execute(ins: Map[String, T])(outs: Outs): Unit = {
    action(ins, outs)
  }
}

object ActionDsl {
  implicit class SendResultToOutput[R](result: R) {
    def =>>(out: ActorRef) = {
      out ! ResultMessage(result)
    }
    def =>>(out: (String, ActorRef)) = {
      out._2 ! ResultMessage(result)
    }
  }
}
