package pl.edu.agh.messages

sealed trait Message

case object StartMessage extends Message
