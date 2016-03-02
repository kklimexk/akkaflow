package pl.edu.agh.workflow_patterns

import akka.actor.ActorRef

trait IPattern

trait Pattern[T, K] extends IPattern {
  val actor: ActorRef
}
