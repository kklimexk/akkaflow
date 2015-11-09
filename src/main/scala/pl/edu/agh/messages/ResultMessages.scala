package pl.edu.agh.messages

sealed trait RMessage

case class ResultMessage[T](res: T) extends RMessage
