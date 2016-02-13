package pl.edu.agh.workflow

import pl.edu.agh.flows.Out

trait Runnable[K] {
  def run: Seq[Out[K]]
}
