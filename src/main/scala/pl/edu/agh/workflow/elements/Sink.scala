package pl.edu.agh.workflow.elements

import akka.actor.{ActorContext, ActorLogging, FSM, Props}
import pl.edu.agh.messages.{Get, GetGroupedOut, GetOut, ResultMessage}
import pl.edu.agh.utils.FSMStates._

import scala.concurrent.duration._

class Sink[R](stateTimeout: FiniteDuration) extends FSM[State, List[R]] with ActorLogging {

  startWith(Init, List.empty[R])
  //println("START OUT: " + self.path + " with state: " + stateName)

  when(Init) {
    case Event(ResultMessage(data: R), out) =>
      //println("out result message (Init): " + out + " actor: " + self.path)
      goto(Active) using (out :+ data)
  }

  when(Active, stateTimeout = stateTimeout) {
    case Event(ResultMessage(data: R), out) =>
      //println("out result message: " + out + " actor: " + self.path)
      stay using (out :+ data)
    case Event(Flush | StateTimeout, out) =>
      //println("out flush: " + out + " actor: " + self.path)
      goto(Idle) using out
  }

  whenUnhandled {
    /*case Event(CheckState, _) =>
      sender ! stateName
      stay*/
    case Event(Get, _) =>
      sender ! this
      stay
  }

  when(Idle) {
    case Event(GetOut, out) =>
      //println("get out: " + out + " actor: " + self.path)
      val o = out
      sender ! o
      goto(Init) using out
    case Event(GetGroupedOut(size), out) =>
      val o = out
      sender ! o.grouped(size)
      goto(Init) using out
  }

  initialize()
}

object Sink {
  def apply[R](context: ActorContext)(stateTimeout: FiniteDuration) = context.actorOf(Sink.props(stateTimeout))
  def apply[R](name: String, context: ActorContext)(stateTimeout: FiniteDuration) = context.actorOf(Sink.props(stateTimeout), name)
  def props[R](stateTimeout: FiniteDuration) = Props(classOf[Sink[R]], stateTimeout)
}
