package pl.edu.agh.messages

import pl.edu.agh.workflow_processes.Pattern
import pl.edu.agh.workflow_processes.synchronization.Sync

sealed trait PropagateDataMessage

case class PropagateData[T, R](elem: Pattern[T, R]) extends PropagateDataMessage
case class PropagateDataForSync[T, R](elem: Sync[T, R], uniqueId: Int) extends PropagateDataMessage
