package pl.edu.agh.messages

sealed trait ResultMessage

case class IntResultMessage(res: Int) extends ResultMessage
