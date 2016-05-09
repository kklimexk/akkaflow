package pl.edu.agh

import pl.edu.agh.actions.Ins

package object workflow_processes {

  import pl.edu.agh.actions.Outs
  import pl.edu.agh.workflow_processes.simple.Process._
  import pl.edu.agh.workflow_processes.synchronization.Sync.{apply => applySync}

  def Process[T, R](name: String, numOfOuts: Int, action: (T, Outs) => Unit) = apply[T, R](name, numOfOuts, action)
  def Process[T, R](name: String, outs: Seq[String], action: (T, Outs) => Unit) = apply[T, R](name, outs, action)
  def Process[T, R](name: String, numOfOuts: Int, action: T => Outs => Unit) = apply[T, R](name, numOfOuts, action)
  def Process[T, R](name: String, outs: Seq[String], action: T => Outs => Unit) = apply[T, R](name, outs, action)

  def Sync[T, R](name: String, numOfIns: Int, numOfOuts: Int, action: (Ins[T], Outs) => Unit) =
    applySync[T, R](name, numOfIns, numOfOuts, action)

  def Sync[T, R](name: String, numOfIns: Int, outs: Seq[String], action: (Ins[T], Outs) => Unit)(implicit d: DummyImplicit) =
    applySync[T, R](name, numOfIns, outs, action)

  def Sync[T, R](name: String, ins: Seq[String], numOfOuts: Int, action: (Ins[T], Outs) => Unit) =
    applySync[T, R](name, ins, numOfOuts, action)

  def Sync[T, R](name: String, ins: Seq[String], outs: Seq[String], action: (Ins[T], Outs) => Unit) =
    applySync[T, R](name, ins, outs, action)

  def Sync[T, R](name: String, numOfIns: Int, numOfOuts: Int, action: Ins[T] => Outs => Unit) =
    applySync[T, R](name, numOfIns, numOfOuts, action)

  def Sync[T, R](name: String, numOfIns: Int, outs: Seq[String], action: Ins[T] => Outs => Unit)(implicit d: DummyImplicit) =
    applySync[T, R](name, numOfIns, outs, action)

  def Sync[T, R](name: String, ins: Seq[String], numOfOuts: Int, action: Ins[T] => Outs => Unit) =
    applySync[T, R](name, ins, numOfOuts, action)

  def Sync[T, R](name: String, ins: Seq[String], outs: Seq[String], action: Ins[T] => Outs => Unit) =
    applySync[T, R](name, ins, outs, action)
}
