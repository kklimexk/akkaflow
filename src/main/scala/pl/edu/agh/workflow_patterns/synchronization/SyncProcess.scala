package pl.edu.agh.workflow_patterns.synchronization

import pl.edu.agh.workflow_patterns.WorkflowProcess

trait SyncProcess[K] extends WorkflowProcess {
  protected var _out = List.empty[K]
  def out = {
    _out
  }
}
