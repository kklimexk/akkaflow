package pl.edu.agh.messages

sealed trait DataMessage

case class IntDataMessage(number: Int) extends DataMessage
