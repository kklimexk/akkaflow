package pl.edu.agh.workflow_patterns.merge

//Merge
class Merge[T] {
  lazy val mergeActor = MergeActor[T]
}

object Merge {
  def apply[T] = new Merge[T]
}
