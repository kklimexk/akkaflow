package pl.edu.agh.flows

import akka.actor.{ActorContext, ActorLogging, Props, Actor}
import pl.edu.agh.messages.{GetGroupedOut, GetOut, Get, ResultMessage}

class Sink[K] extends Actor with ActorLogging {

  var out = List.empty[K]

  def receive = {
    case ResultMessage(data: K) =>
      //log.info("CHILD")
      out :+= data
    case GetOut =>
      sender ! out
    case GetGroupedOut(size: Int) =>
      sender ! out.grouped(size)
    case Get =>
      sender ! this
  }

}

object Sink {
  def apply[K](context: ActorContext) = context.actorOf(Sink.props)
  def apply[K](name: String, context: ActorContext) = context.actorOf(Sink.props, name)
  def props[K] = Props[Sink[K]]
}
