package pl.edu.agh.actions

import akka.actor.ActorRef

case class Outs(outs: Map[String, ActorRef]) {
  def apply(): Map[String, ActorRef] = outs
  def apply(outName: String): ActorRef = outs(outName)
  def apply(outNo: Int): ActorRef = outs.toSeq(outNo)._2

  def size = outs.size
}

object Outs {
  object Implicits {
    implicit class OutTuple2ActorRef(out: ActorRef) {
      def isEqualTo(tupleOut: (String, ActorRef)): Boolean = out.eq(tupleOut._2)
      def isNotEqualTo(tupleOut: (String, ActorRef)): Boolean = !out.eq(tupleOut._2)
    }
  }
}
