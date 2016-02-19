package pl.edu.agh.workflow_patterns

import akka.actor.ActorRef

trait Pattern[T, K] {
  val actor: ActorRef
}
