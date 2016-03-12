package pl.edu.agh.workflow_patterns.merge

import akka.actor.{Props, ActorLogging, Actor}
import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.messages.{ResultMessage, DataMessage, Get}

class MergeActor[T, R](val numOfOuts: Int, action: ISingleAction[T, R], sendTo: String) extends Actor with MergeProcess[T, R] with ActorLogging {
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

  def apply[T, R](name: String, numOfOuts: Int, action: ISingleAction[T, R], sendTo: String) = system.actorOf(MergeActor.props(numOfOuts, action, sendTo), name)
  def props[T, R](numOfOuts: Int, action: ISingleAction[T, R], sendTo: String) = Props(classOf[MergeActor[T, R]], numOfOuts, action, sendTo)
}
