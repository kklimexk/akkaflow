package pl.edu.agh.workflow_patterns

import akka.actor.Actor
import pl.edu.agh.actions.Transformation

abstract class PatternActor(val numOfOuts: Int, action: Transformation) extends Actor
