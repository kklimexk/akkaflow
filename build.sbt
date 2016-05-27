name := """akkaflow"""

version := "4.3"

scalaVersion := "2.11.7"

lazy val akkaVersion = "2.4.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.5"
libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.7"
