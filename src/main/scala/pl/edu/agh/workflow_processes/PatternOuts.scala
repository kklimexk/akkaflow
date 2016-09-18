package pl.edu.agh.workflow_processes

import akka.actor.ActorRef
import pl.edu.agh.workflow.elements.Sink
import scala.concurrent.duration._

trait PatternOuts[R] { actor: PatternActor =>
  /**
    * Automatically generated outputs or user defined outputs
    */
  protected var _outs = {
    var outsMap = Map.empty[String, ActorRef]
    if (actor.numOfOuts > 0) {
      for (i <- 0 until actor.numOfOuts) {
        outsMap += (("out" + i) -> Sink[R]("out" + i, actor.context)(PatternOuts.stateTimeout))
      }
    } else if (actor._outputs.nonEmpty) {
      outsMap = _outputs.map(o => o -> Sink[R](o, actor.context)(PatternOuts.stateTimeout))(collection.breakOut)
    }
    outsMap
  }
  def outs = _outs.values.toSeq
  def outs(name: String) = _outs(name)
}

object PatternOuts {
  var stateTimeout = 1.seconds
}
