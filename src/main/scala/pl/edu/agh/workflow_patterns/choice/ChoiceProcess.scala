package pl.edu.agh.workflow_patterns.choice

import pl.edu.agh.workflow_patterns.WorkflowProcess

trait ChoiceProcess extends WorkflowProcess {
  var res: Int = _

  var out1 = List.empty[Int]
  var out2 = List.empty[Int]
  var out3 = List.empty[Int]
}
