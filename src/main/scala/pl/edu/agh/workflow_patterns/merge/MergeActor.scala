package pl.edu.agh.workflow_patterns.merge

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.flows.Sink
import pl.edu.agh.messages.{ResultMessage, DataMessage, Get}

class MergeActor[T, K](numOfOuts: Int, action: ISingleAction[T, K], sendTo: String) extends Actor with MergeProcess[K] with ActorLogging {

  protected var _outs = {
    var outsSeq = Seq.empty[ActorRef]
    for (i <- 0 until numOfOuts) {
      outsSeq :+= Sink[K]("out" + i, context)
    }
    outsSeq
  }

  def outs = _outs

  def receive = {
    case DataMessage(data: T) =>
      //log.info("DATA: {}", data)
      val res = action.execute(data)
      context.actorSelection(self.path + "/" + sendTo) ! ResultMessage(res)
    case Get =>
      sender ! this
  }
}

object MergeActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, K](name: String, numOfOuts: Int, action: ISingleAction[T, K], sendTo: String) = system.actorOf(MergeActor.props(numOfOuts, action, sendTo), name)
  def props[T, K](numOfOuts: Int, action: ISingleAction[T, K], sendTo: String) = Props(classOf[MergeActor[T, K]], numOfOuts, action, sendTo)
}
