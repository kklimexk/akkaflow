package pl.edu.agh.flows

sealed trait ISource
case class Source(data: Range) extends ISource
case class StringSource(data: String*) extends ISource
