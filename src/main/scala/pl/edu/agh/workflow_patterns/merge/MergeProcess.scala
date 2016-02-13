package pl.edu.agh.workflow_patterns.merge

import pl.edu.agh.workflow_patterns.WorkflowProcess

trait MergeProcess[K] extends WorkflowProcess {
  var _out = List.empty[K]
  def out = {
    //TODO: To trzeba zmienic
    Thread.sleep(200)
    _out
  }
}
