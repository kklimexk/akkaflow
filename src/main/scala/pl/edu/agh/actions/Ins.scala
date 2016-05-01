package pl.edu.agh.actions

case class Ins[T](ins: Map[String, T]) {
  def apply(): Map[String, T] = ins
  def apply(outName: String): T = ins(outName)
  def apply(outNo: Int): T = ins.toSeq(outNo)._2
}
