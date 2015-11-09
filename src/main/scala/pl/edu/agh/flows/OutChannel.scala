package pl.edu.agh.flows

import akka.actor.{ActorSystem, Props, Actor}
import pl.edu.agh.messages.ResultMessage

class OutChannel extends Actor {
  def receive = {
    case ResultMessage(res) =>
      println("Result is: " + res)
      context.system.terminate()
  }
}

object OutChannel {
  val props = Props[OutChannel]

  def apply(system: ActorSystem, name: String) = system.actorOf(OutChannel.props, name)
}
