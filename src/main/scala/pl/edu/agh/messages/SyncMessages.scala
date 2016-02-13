package pl.edu.agh.messages

import pl.edu.agh.workflow_patterns.synchronization.MultipleSync

sealed trait SyncMessage
case class PropagateDataForMultipleSync[T, K](elem: MultipleSync[T, K], uniqueId: Int) extends SyncMessage
