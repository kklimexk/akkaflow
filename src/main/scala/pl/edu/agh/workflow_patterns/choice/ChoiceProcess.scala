package pl.edu.agh.workflow_patterns.choice

import pl.edu.agh.workflow_patterns.WorkflowProcess

trait ChoiceProcess[K] extends WorkflowProcess {
  var out1 = List.empty[K]
  var out2 = List.empty[K]
  var out3 = List.empty[K]
}
