package pl.edu.agh.messages

sealed trait DMessage

case class DataMessage[T](data: T) extends DMessage

case class SyncDataMessage1[T](data: T) extends DMessage
case class SyncDataMessage2[T](data: T) extends DMessage
