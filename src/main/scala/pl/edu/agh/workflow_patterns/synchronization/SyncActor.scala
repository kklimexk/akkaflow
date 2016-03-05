package pl.edu.agh.workflow_patterns.synchronization

import akka.actor.{ActorRef, ActorLogging, Props, Actor}
import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.flows.Sink
import pl.edu.agh.messages._

//Synchronization Pattern
class SyncActor[T, R](numOfOuts: Int, action: ISingleAction[T, R], sendTo: String) extends Actor with SyncProcess[R] with ActorLogging {

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
      //log.info("Computing action: {}", res)
      context.actorSelection(self.path + "/" + sendTo) ! ResultMessage(res)
    case Get =>
      sender ! this
  }
}

object SyncActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, R](name: String, numOfOuts: Int, action: ISingleAction[T, R], sendTo: String) = system.actorOf(SyncActor.props(numOfOuts, action, sendTo), name)
  def props[T, R](numOfOuts: Int, action: ISingleAction[T, R], sendTo: String) = Props(classOf[SyncActor[T, R]], numOfOuts, action, sendTo)
}
