package pl.edu.agh.flows

import java.util.concurrent.ConcurrentLinkedQueue

import akka.actor.{ActorContext, ActorLogging, Props, Actor}
import pl.edu.agh.messages.{GetGroupedOut, GetOut, Get, ResultMessage}

class Sink[R] extends Actor with ActorLogging {

  var out = new ConcurrentLinkedQueue[R]()

  def receive = {
    case ResultMessage(data: R) =>
      //log.info("CHILD")
      out.offer(data)
    case GetOut =>
      if (out.peek() != null) sender ! out.poll()
      else sender ! None
    case GetGroupedOut(size: Int) =>
      var tmpOut = List.empty[R]
      for (i <- 0 until size) if (out.peek() != null) tmpOut :+= out.poll()
      if (tmpOut.size == size) sender ! tmpOut else sender ! None
    case Get =>
      sender ! this
  }

}

object Sink {
  def apply[R](context: ActorContext) = context.actorOf(Sink.props)
  def apply[R](name: String, context: ActorContext) = context.actorOf(Sink.props, name)
  def props[R] = Props[Sink[R]]
}
