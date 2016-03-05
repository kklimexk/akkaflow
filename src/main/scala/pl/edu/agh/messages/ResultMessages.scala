package pl.edu.agh.messages

sealed trait RMessage

case class ResultMessage[R](res: R) extends RMessage

case object GetResult extends RMessage
case object GetOut extends RMessage
case class GetGroupedOut(size: Int) extends RMessage
