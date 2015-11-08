package pl.edu.agh.flows

import akka.actor.{ActorSystem, Props, Actor}
import pl.edu.agh.messages.IntResultMessage

class OutChannel extends Actor {
  def receive = {
    case IntResultMessage(res) =>
      println("Result is: " + res)
      context.system.terminate()
  }
}

object OutChannel {
  var name = "out"
  val props = Props[OutChannel]

  def apply(system: ActorSystem) = system.actorOf(OutChannel.props, name)
}

trait OutChannelHandler { actor: Actor =>
  private val actorSystem = actor.context.system
  val out = actorSystem.actorSelection(actorSystem + "/user/" + OutChannel.name)
}
