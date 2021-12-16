import com.typesafe.sbt.packager.docker.Cmd

enablePlugins(JavaServerAppPackaging, DebianPlugin, SystemdPlugin)

name := "bart-pi"
version := "0.2"
maintainer := "Dylan Owen"

scalaVersion := "2.12.6"

libraryDependencies += Dependencies.Akka
libraryDependencies ++= Dependencies.Sttp
libraryDependencies ++= Dependencies.Circe
libraryDependencies += Dependencies.Pi4J

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

mainClass in Compile := Some("com.dylowen.bartpi.BartPi")

packageSummary := "Bart-Pi"
packageDescription := "Bart Pi Display Board"
daemonUser in Linux := "pi"