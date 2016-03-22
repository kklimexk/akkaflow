package pl.edu.agh.messages

import pl.edu.agh.actions.Transformation

sealed trait Message

case object Get extends Message

case class ChangeAction(action: Transformation) extends Message
case class ChangeSendTo(outName: String) extends Message
