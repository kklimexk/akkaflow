package pl.edu.agh.workflow_patterns

trait WorkflowProcess {
  var res: Int = _
  var _out = List.empty[Int]
  def out = {
    _out
  }
}
