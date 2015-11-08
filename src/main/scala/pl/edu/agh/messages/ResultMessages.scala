package pl.edu.agh.messages

sealed trait ResultMessage

case object Result extends ResultMessage
case class IntResultMessage(res: Int) extends ResultMessage
