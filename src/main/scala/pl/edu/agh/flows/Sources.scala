package pl.edu.agh.flows

sealed trait ISource
case class Source(data: Range) extends ISource
case class StringSource(data: String*) extends ISource
case class AnySource(data: Any*) extends ISource
case class AnyRangeSource(data: Range) extends ISource
