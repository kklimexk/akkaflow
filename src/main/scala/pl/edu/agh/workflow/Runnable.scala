package pl.edu.agh.workflow

import pl.edu.agh.flows.Out

trait Runnable {
  def run: Seq[Out]
}
