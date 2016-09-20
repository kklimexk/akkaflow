package pl.edu.agh.workflow_processes

import akka.actor.FSM
import pl.edu.agh.actions.Transformation
import pl.edu.agh.utils.FSMStates.{Data, State}

abstract class PatternActor(val numOfOuts: Int, val _outputs: Seq[String], action: Transformation) extends FSM[State, Data]
