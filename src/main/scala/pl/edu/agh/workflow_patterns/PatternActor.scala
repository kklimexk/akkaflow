package pl.edu.agh.workflow_patterns

import akka.actor.Actor
import pl.edu.agh.actions.Transformation

abstract class PatternActor(val numOfOuts: Int, val outputs: Seq[String], action: Transformation) extends Actor
