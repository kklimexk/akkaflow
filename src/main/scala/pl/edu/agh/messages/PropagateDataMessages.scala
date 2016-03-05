package pl.edu.agh.messages

import pl.edu.agh.workflow_patterns.Pattern
import pl.edu.agh.workflow_patterns.synchronization.MultipleSync

sealed trait PropagateDataMessage

case class PropagateData[T, R](elem: Pattern[T, R]) extends PropagateDataMessage
case class PropagateDataForMultipleSync[T, R](elem: MultipleSync[T, R], uniqueId: Int) extends PropagateDataMessage
