package pl.edu.agh.workflow_patterns.merge

trait MergeProcess {
  var _out = List.empty[Int]
  def out = {
    //TODO: To trzeba zmienic
    Thread.sleep(200)
    _out
  }
}
