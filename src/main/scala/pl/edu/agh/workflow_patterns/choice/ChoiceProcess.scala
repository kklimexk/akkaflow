package pl.edu.agh.workflow_patterns.choice

import pl.edu.agh.workflow_patterns.WorkflowProcess

import scala.collection.mutable.ListBuffer

abstract class ChoiceProcess[T, K](numOfIns: Int, numOfOuts: Int) extends WorkflowProcess {

  var ins = {
    var insSeq = Seq.empty[List[T]]
    for (i <- 0 until numOfIns) {
      insSeq :+= List.empty[T]
    }
    insSeq
  }

  var outs = {
    var outsSeq = Seq.empty[ListBuffer[K]]
    for (i <- 0 until numOfOuts) {
      outsSeq :+= ListBuffer.empty[K]
    }
    outsSeq
  }

}
