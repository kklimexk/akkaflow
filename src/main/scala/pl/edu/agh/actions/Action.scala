package pl.edu.agh.actions

case class Action[T: Acceptable](action: T => Int) {
  def execute(in: T): Int = {
    action(in)
  }
}

sealed abstract class Acceptable[T]
object Acceptable {
  implicit object Int extends Acceptable[Int]
  implicit object ListOfInt extends Acceptable[List[Int]]
}
