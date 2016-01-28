package pl.edu.agh.messages

import pl.edu.agh.workflow_patterns.synchronization.MultipleSync

sealed trait SyncMessage
case class PropagateDataForMultipleSync[T](elem: MultipleSync[T], uniqueId: Int) extends SyncMessage
