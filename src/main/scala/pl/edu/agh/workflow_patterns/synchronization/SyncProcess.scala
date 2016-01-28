package pl.edu.agh.workflow_patterns.synchronization

import pl.edu.agh.workflow_patterns.WorkflowProcess

trait SyncProcess extends WorkflowProcess {
  var res: Int = _
  var _out = List.empty[Int]
  def out = {
    _out
  }
}
