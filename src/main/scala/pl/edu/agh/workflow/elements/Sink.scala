package pl.edu.agh.workflow.elements

import akka.actor.{ActorContext, FSM, Props}
import pl.edu.agh.messages.{Get, GetGroupedOut, GetOut, ResultMessage}
import pl.edu.agh.utils.FSMStates._
import pl.edu.agh.utils.ActorUtils.Implicits._

import scala.concurrent.duration._

class Sink[R](stateTimeout: FiniteDuration) extends FSM[State, List[R]] {

  startWith(Init, List.empty[R])

  val parent = context.parent.toPatternActor
  //println("START OUT: " + self.path + " with state: " + stateName)

  when(Init) {
    case Event(ResultMessage(data: R), out) =>
      //println("out result message (Init): " + out + " actor: " + self.path)
      goto(Active) using (out :+ data)
  }

  when(Active) {
    case Event(ResultMessage(data: R), out) =>
      //println("out result message: " + out + " actor: " + self.path)
      stay using (out :+ data)
    case Event(Flush | StateTimeout, out) =>
      //println("out flush: " + out + " actor: " + self.path)
      parent.stateName match {
        case Idle => goto(Idle) using out
        case activeOrInit @ (Active | Init) =>
          setTimer("stateTimeout", Flush, stateTimeout); stay using out
      }
    case Event(GetOut, out) =>
      sender ! NotReady
      stay using out
    case Event(GetGroupedOut(size), out) =>
      sender ! NotReady
      stay using out
  }

  when(Idle) {
    case Event(ResultMessage(data: R), out) =>
      //println("out result message (Init): " + out + " actor: " + self.path)
      goto(Active) using (out :+ data)
    case Event(GetOut, out) =>
      //println("get out: " + out + " actor: " + self.path)
      val o = out
      sender ! o
      stay using out
    case Event(GetGroupedOut(size), out) =>
      val o = out
      sender ! o.grouped(size)
      stay using out
  }

  onTransition {
    case _ -> Active => setTimer("stateTimeout", Flush, stateTimeout)
  }

  whenUnhandled {
    case Event(Get, _) =>
      sender ! this
      stay
  }

  initialize()
}

object Sink {
  def apply[R](context: ActorContext)(stateTimeout: FiniteDuration) = context.actorOf(Sink.props(stateTimeout))
  def apply[R](name: String, context: ActorContext)(stateTimeout: FiniteDuration) = context.actorOf(Sink.props(stateTimeout), name)
  def props[R](stateTimeout: FiniteDuration) = Props(classOf[Sink[R]], stateTimeout)
}
