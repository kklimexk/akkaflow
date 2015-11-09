package pl.edu.agh.messages

sealed trait DMessage

case class DataMessage[T](data: T) extends DMessage
