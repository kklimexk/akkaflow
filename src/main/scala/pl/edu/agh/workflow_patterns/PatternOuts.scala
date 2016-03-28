package pl.edu.agh.workflow_patterns

import akka.actor.ActorRef
import pl.edu.agh.flows.Sink

trait PatternOuts[R] { actor: PatternActor =>
  protected var _outs = {
    var outsSeq = Seq.empty[ActorRef]
    for (i <- 0 until actor.numOfOuts) {
      outsSeq :+= Sink[R]("out" + i, actor.context)
    }
    outsSeq
  }
  protected var userDefinedOuts = {
    val outsMap: Map[String, ActorRef] = outputs.map(o => o -> Sink[R](o, actor.context))(collection.breakOut)
    outsMap
  }
  def outs = _outs
  def outs(name: String) = userDefinedOuts(name)
}
