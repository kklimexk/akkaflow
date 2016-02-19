package pl.edu.agh.messages

import pl.edu.agh.workflow_patterns.Pattern

sealed trait MergeMessage
case class PropagateDataForMerge[T, K](elem: Pattern[T, K]) extends MergeMessage
