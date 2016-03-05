package pl.edu.agh.workflow

import pl.edu.agh.flows.{In, Out}
import pl.edu.agh.utils.ActorUtils._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class Workflow[T, R](name: String, numOfIns: Int, numOfOuts: Int, block: => (Seq[In[T]], Seq[Out[R]]) => Future[Seq[Out[R]]]) extends AbstractWorkflow[T, R](name, numOfIns, numOfOuts) with Runnable[R] {
  def run: Seq[Out[R]] = {
    val resF = block(ins, outs)
    Await.ready(resF, Duration.Inf)
    system.terminate
    outs
  }
}

object Workflow {
  def apply[T, R](block: => (Seq[In[T]], Seq[Out[R]]) => Future[Seq[Out[R]]]) = new Workflow[T, R]("", 1, 1, block)
  def apply[T, R](name: String, block: => (Seq[In[T]], Seq[Out[R]]) => Future[Seq[Out[R]]]) = new Workflow[T, R](name, 1, 1, block)
  def apply[T, R](name: String, numOfIns: Int, block: => (Seq[In[T]], Seq[Out[R]]) => Future[Seq[Out[R]]]) = new Workflow[T, R](name, numOfIns, 1, block)
  def apply[T, R](name: String, numOfIns: Int, numOfOuts: Int, block: => (Seq[In[T]], Seq[Out[R]]) => Future[Seq[Out[R]]]) = new Workflow[T, R](name, numOfIns, numOfOuts, block)
}
