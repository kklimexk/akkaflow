package pl.edu.agh.workflow_patterns.split

import akka.actor.{ActorLogging, Props, ActorRef, Actor}
import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.flows.Sink
import pl.edu.agh.messages.{ResultMessage, DataMessage, Get}

class SplitActor[T, R](numOfOuts: Int, action: ISingleAction[T, R]) extends Actor with SplitProcess[R] with ActorLogging {

  protected var _outs = {
    var outsSeq = Seq.empty[ActorRef]
    for (i <- 0 until numOfOuts) {
      outsSeq :+= Sink[R]("out" + i, context)
    }
    outsSeq
  }

  def outs = _outs

  def receive = {
    case DataMessage(data: T) =>
      val res = action.execute(data)
      outs foreach (_ ! ResultMessage(res))
    case Get =>
      sender ! this
  }
}

object SplitActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, R](name: String, numOfOuts: Int, action: ISingleAction[T, R]) = system.actorOf(SplitActor.props(numOfOuts, action), name)
  def props[T, R](numOfOuts: Int, action: ISingleAction[T, R]) = Props(classOf[SplitActor[T, R]], numOfOuts, action)
}
