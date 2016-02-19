package pl.edu.agh.messages

import pl.edu.agh.workflow_patterns.Pattern
import pl.edu.agh.workflow_patterns.synchronization.MultipleSync

sealed trait PropagateDataMessage

case class PropagateData[T, K](elem: Pattern[T, K]) extends PropagateDataMessage
case class PropagateDataForMultipleSync[T, K](elem: MultipleSync[T, K], uniqueId: Int) extends PropagateDataMessage
