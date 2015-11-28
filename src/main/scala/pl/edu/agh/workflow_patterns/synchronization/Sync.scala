package pl.edu.agh.workflow_patterns.synchronization

import akka.actor.{ActorLogging, ActorSystem, Props, Actor}
import pl.edu.agh.messages._

//Synchronization Pattern
class Sync extends Actor with ActorLogging {

  var sqr = 1
  var sum = 0
  var out = List.empty[Int]

  def receive = {
    case DataMessage(data: Int) =>
      sqr = data * data
      log.info("Computing sqr action: {}", sqr)
      out :+= sqr
    case DataMessage(data: List[Int]) =>
      sum = data.reduceLeft[Int](_+_)
      log.info("Computing sum action: {}", sum)
      out :+= sum
    case Get =>
      sender ! this
  }
}

object Sync {
  def apply(name: String)(implicit system: ActorSystem) = system.actorOf(Sync.props, name)
  def props = Props[Sync]
}
