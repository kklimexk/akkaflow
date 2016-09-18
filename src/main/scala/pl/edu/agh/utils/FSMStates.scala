package pl.edu.agh.utils

object FSMStates {
  sealed trait State

  case object Idle extends State
  case object Active extends State
  case object Init extends State

  case object Flush
}
