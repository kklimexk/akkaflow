package pl.edu.agh.workflow_processes.synchronization

import pl.edu.agh.actions.{ActionConverter, Ins, Outs}

object SyncDsl {
  implicit class SetSyncProperties[T, R](sync: Sync[T, R]) {
    def name(name: String): Sync[T, R] = {
      sync._name = name
      sync
    }
    def inputs(ins: Seq[String]): Sync[T, R] = {
      sync._ins = ins
      sync
    }
    def inputs(ins: Product): Sync[T, R] = {
      sync._ins = ins.productIterator.toList.map(_.toString)
      sync
    }
    def outputs(outs: Seq[String]): Sync[T, R] = {
      sync._outs = outs
      sync
    }
    def outputs(outs: Product): Sync[T, R] = {
      sync._outs = outs.productIterator.toList.map(_.toString)
      sync
    }
    def numOfIns(numOfIns: Int): Sync[T, R] = {
      sync._numOfIns = numOfIns
      sync
    }
    def numOfOuts(numOfOuts: Int): Sync[T, R] = {
      sync._numOfOuts = numOfOuts
      sync
    }
    def action(action: (Ins[T], Outs) => Unit): Sync[T, R] = {
      sync._action = ActionConverter(action)
      sync
    }
    def action(action: Ins[T] => Outs => Unit): Sync[T, R] = {
      sync._action = ActionConverter(action)
      sync
    }
  }
}
