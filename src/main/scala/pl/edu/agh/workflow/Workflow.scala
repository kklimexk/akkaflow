package pl.edu.agh.workflow

import pl.edu.agh.utils.ActorUtils._
import pl.edu.agh.workflow.elements.{In, Out}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

trait IWorkflow

class Workflow[T, R](name: String, numOfIns: Int, numOfOuts: Int, var block: (Seq[In[T]], Seq[Out[R]]) => Future[Seq[Out[R]]]) extends AbstractWorkflow[T, R](name, numOfIns, numOfOuts) with Runnable[R] with IWorkflow {
  def run: Seq[Out[R]] = {
    val resF = block(ins, outs)
    Await.ready(resF, Duration.Inf)
    system.terminate
    outs
  }
  def start = {
    val resF = block(ins, outs)
    Await.ready(resF, Duration.Inf)
  }
  def stop = {
    system.terminate
  }
}

object Workflow {
  def apply[T, R](block: => (Seq[In[T]], Seq[Out[R]]) => Future[Seq[Out[R]]]) = new Workflow[T, R]("", 1, 1, block)
  def apply[T, R](name: String, block: => (Seq[In[T]], Seq[Out[R]]) => Future[Seq[Out[R]]]) = new Workflow[T, R](name, 1, 1, block)
  def apply[T, R](name: String, numOfIns: Int, block: => (Seq[In[T]], Seq[Out[R]]) => Future[Seq[Out[R]]]) = new Workflow[T, R](name, numOfIns, 1, block)
  def apply[T, R](name: String, numOfIns: Int, numOfOuts: Int, block: => (Seq[In[T]], Seq[Out[R]]) => Future[Seq[Out[R]]]) = new Workflow[T, R](name, numOfIns, numOfOuts, block)
}
