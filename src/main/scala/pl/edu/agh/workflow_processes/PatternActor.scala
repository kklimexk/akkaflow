package pl.edu.agh.workflow_processes

import akka.actor.Actor
import pl.edu.agh.actions.Transformation

abstract class PatternActor(val numOfOuts: Int, val _outputs: Seq[String], action: Transformation) extends Actor
