package pl.edu.agh.workflow_patterns

import akka.actor.ActorRef
import pl.edu.agh.flows.Sink

trait PatternOuts[R] { actor: PatternActor =>
  /**
    * Automatically generated outputs
    */
  protected var _outs = {
    var outsMap = Map.empty[String, ActorRef]
    for (i <- 0 until actor.numOfOuts) {
      outsMap += (("out" + i) -> Sink[R]("out" + i, actor.context))
    }
    outsMap
  }
  /**
    * User defined outputs
    */
  protected var userDefinedOuts = {
    val outsMap: Map[String, ActorRef] = outputs.map(o => o -> Sink[R](o, actor.context))(collection.breakOut)
    outsMap
  }
  def outs = _outs.values.toSeq
  def outs(name: String) = userDefinedOuts(name)
}
