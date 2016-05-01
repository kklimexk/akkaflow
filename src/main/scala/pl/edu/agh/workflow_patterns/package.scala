package pl.edu.agh

package object workflow_patterns {

  import pl.edu.agh.actions.Outs
  import pl.edu.agh.workflow_patterns.merge.Merge._
  import pl.edu.agh.workflow_patterns.synchronization.Sync.{apply => applySync}

  def Merge[T, R](name: String, numOfOuts: Int, action: (T, Outs) => Unit) = apply[T, R](name, numOfOuts, action)
  def Merge[T, R](name: String, outs: Seq[String], action: (T, Outs) => Unit) = apply[T, R](name, outs, action)

  def Split[T, R](name: String, numOfOuts: Int, action: (T, Outs) => Unit) = apply[T, R](name, numOfOuts, action)
  def Split[T, R](name: String, outs: Seq[String], action: (T, Outs) => Unit) = apply[T, R](name, outs, action)

  def Choice[T, R](name: String, numOfOuts: Int, action: (T, Outs) => Unit) = apply[T, R](name, numOfOuts, action)
  def Choice[T, R](name: String, outs: Seq[String], action: (T, Outs) => Unit) = apply[T, R](name, outs, action)

  def Sync[T, R](name: String, numOfIns: Int, numOfOuts: Int, action: (Seq[T], Outs) => Unit) =
    applySync[T, R](name, numOfIns, numOfOuts, action)

  def Sync[T, R](name: String, numOfIns: Int, outs: Seq[String], action: (Seq[T], Outs) => Unit)(implicit d: DummyImplicit) =
    applySync[T, R](name, numOfIns, outs, action)

  def Sync[T, R](name: String, numOfOuts: Int, ins: Seq[String], action: (Map[String, T], Outs) => Unit) =
    applySync[T, R](name, numOfOuts, ins, action)

  def Sync[T, R](name: String, ins: Seq[String], outs: Seq[String], action: (Map[String, T], Outs) => Unit) =
    applySync[T, R](name, ins, outs, action)
}
