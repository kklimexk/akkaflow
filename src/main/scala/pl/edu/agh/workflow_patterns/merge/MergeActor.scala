package pl.edu.agh.workflow_patterns.merge

import akka.actor.{Props, ActorLogging}
import pl.edu.agh.actions.ISingleAction
import pl.edu.agh.messages._
import pl.edu.agh.workflow_patterns.{PatternOuts, PatternActor}

class MergeActor[T, R](numOfOuts: Int, outs: Seq[String], var action: ISingleAction[T, R], var sendTo: String) extends PatternActor(numOfOuts, outs, action) with PatternOuts[R] with ActorLogging {
  def receive = {
    case DataMessage(data: T) =>
      //log.info("DATA: {}", data)
      val res = action.execute(data)
      context.actorSelection(self.path + "/" + sendTo) ! ResultMessage(res)
    case ChangeAction(act: ISingleAction[T, R]) =>
      action = act
    case ChangeSendTo(outName) =>
      sendTo = outName
    case Get =>
      sender ! this
  }
}

object MergeActor {
  import pl.edu.agh.utils.ActorUtils.system

  def apply[T, R](name: String, numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R], sendTo: String) = system.actorOf(MergeActor.props(numOfOuts, outs, action, sendTo), name)
  def props[T, R](numOfOuts: Int, outs: Seq[String], action: ISingleAction[T, R], sendTo: String) = Props(classOf[MergeActor[T, R]], numOfOuts, outs, action, sendTo)
}
