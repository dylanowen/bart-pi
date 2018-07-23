name := "bart-pi"

version := "0.2"

scalaVersion := "2.12.6"

libraryDependencies += Dependencies.Akka
libraryDependencies ++= Dependencies.Sttp
libraryDependencies ++= Dependencies.Circe
libraryDependencies += Dependencies.Pi4J

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}