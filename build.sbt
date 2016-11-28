name := "bart-pi"

version := "0.1"

// TODO move to 2.12.0
scalaVersion := "2.11.8"

// TODO move to 3.0 when it comes out of RC for akka
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.10",
  "com.typesafe.akka" %% "akka-http-core" % "2.4.11",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11",
  "com.typesafe.akka" %% "akka-http-xml-experimental" % "2.4.11",
  "de.heikoseeberger" %% "akka-http-json4s" % "1.5.3",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.json4s" %% "json4s-jackson" % "3.5.0",
  "org.json4s" %% "json4s-native" % "3.5.0",
  "com.pi4j" % "pi4j-core" % "1.1"
)
    