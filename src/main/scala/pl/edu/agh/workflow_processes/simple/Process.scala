package pl.edu.agh.workflow_processes.simple

import pl.edu.agh.actions._
import pl.edu.agh.workflow_processes.Pattern

/**
  * Simple Process without Synchronization
  */
class Process[T, R](var _name: String,
                    var _numOfOuts: Int,
                    var _outs: Seq[String],
                    var _action: ISingleAction[T, R]) extends Pattern[T, R] {
  def this() {
    this("", 0, Seq.empty[String], EmptySingleAction[T, R]())
  }

  override lazy val actor = ProcessActor(_name, _numOfOuts, _outs, _action)
}

object Process {
  def apply[T, R](name: String, numOfOuts: Int, action: (T, Outs) => Unit) = new Process[T, R](name, numOfOuts, Seq.empty, ActionConverter[T, R](action))
  def apply[T, R](name: String, outs: Seq[String], action: (T, Outs) => Unit) = new Process[T, R](name, 0, outs, ActionConverter[T, R](action))
  def apply[T, R](name: String, numOfOuts: Int, action: T => Outs => Unit) = new Process[T, R](name, numOfOuts, Seq.empty, ActionConverter[T, R](action))
  def apply[T, R](name: String, outs: Seq[String], action: T => Outs => Unit) = new Process[T, R](name, 0, outs, ActionConverter[T, R](action))
  def apply[T, R] = new Process[T, R]
}
