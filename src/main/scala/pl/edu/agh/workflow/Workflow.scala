package pl.edu.agh.workflow

import pl.edu.agh.flows.{In, Out}
import pl.edu.agh.utils.ActorUtils._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class Workflow[T, K](name: String, numOfIns: Int, numOfOuts: Int, block: => (Seq[In[T]], Seq[Out[K]]) => Future[Seq[Out[K]]]) extends AbstractWorkflow[T, K](name, numOfIns, numOfOuts) with Runnable[K] {
  def run: Seq[Out[K]] = {
    val resF = block(ins, outs)
    Await.ready(resF, Duration.Inf)
    system.terminate
    outs
  }
}

object Workflow {
  def apply[T, K](block: => (Seq[In[T]], Seq[Out[K]]) => Future[Seq[Out[K]]]) = new Workflow[T, K]("", 1, 1, block)
  def apply[T, K](name: String, block: => (Seq[In[T]], Seq[Out[K]]) => Future[Seq[Out[K]]]) = new Workflow[T, K](name, 1, 1, block)
  def apply[T, K](name: String, numOfIns: Int, block: => (Seq[In[T]], Seq[Out[K]]) => Future[Seq[Out[K]]]) = new Workflow[T, K](name, numOfIns, 1, block)
  def apply[T, K](name: String, numOfIns: Int, numOfOuts: Int, block: => (Seq[In[T]], Seq[Out[K]]) => Future[Seq[Out[K]]]) = new Workflow[T, K](name, numOfIns, numOfOuts, block)
}
