package pl.edu.agh.workflow_patterns.synchronization

import akka.actor.{ActorRef, ActorLogging, Props, Actor}
import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.flows.Sink
import pl.edu.agh.messages._

//Synchronization Pattern
class SyncActor[T, K](numOfOuts: Int, action: ISingleAction[T, K], sendTo: String) extends Actor with SyncProcess[K] with ActorLogging {

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
      val res = action.execute(data)
      //log.info("Computing action: {}", res)
      context.actorSelection(self.path + "/" + sendTo) ! ResultMessage(res)
    case Get =>
      sender ! this
  }
}

object SyncActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, K](name: String, numOfOuts: Int, action: ISingleAction[T, K], sendTo: String) = system.actorOf(SyncActor.props(numOfOuts, action, sendTo), name)
  def props[T, K](numOfOuts: Int, action: ISingleAction[T, K], sendTo: String) = Props(classOf[SyncActor[T, K]], numOfOuts, action, sendTo)
}
