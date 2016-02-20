package pl.edu.agh.flows

import akka.actor.{Props, Actor}
import pl.edu.agh.messages.{GetOut, Get, ResultMessage}

class Sink[K] extends Actor {

  var out = List.empty[K]

  def receive = {
    case ResultMessage(data: K) =>
      out :+= data
    case GetOut =>
      sender ! out
    case Get =>
      sender ! this
  }

}

object Sink {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[K]() = system.actorOf(Sink.props)
  def props[K] = Props[Sink[K]]
}
