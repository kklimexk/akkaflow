package pl.edu.agh.messages

import akka.actor.ActorRef

sealed trait Message

case class Dest(actor: ActorRef) extends Message
