package pl.edu.agh.workflow_processes.simple

import pl.edu.agh.actions.{ActionConverter, ISingleAction, Outs}
import pl.edu.agh.workflow_processes.Pattern

/**
  * Simple Process without Synchronization
  */
class Process[T, R](name: String, numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R]) extends Pattern[T, R] {
  override lazy val actor = ProcessActor(name, numOfOuts, outs, action)
}

object Process {
  def apply[T, R](name: String, numOfOuts: Int, action: (T, Outs) => Unit) = new Process[T, R](name, numOfOuts, Seq.empty, ActionConverter[T, R](action))
  def apply[T, R](name: String, outs: Seq[String], action: (T, Outs) => Unit) = new Process[T, R](name, 0, outs, ActionConverter[T, R](action))
  def apply[T, R](name: String, numOfOuts: Int, action: T => Outs => Unit) = new Process[T, R](name, numOfOuts, Seq.empty, ActionConverter[T, R](action))
  def apply[T, R](name: String, outs: Seq[String], action: T => Outs => Unit) = new Process[T, R](name, 0, outs, ActionConverter[T, R](action))
}
