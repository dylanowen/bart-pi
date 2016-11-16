name := "bart-pi"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-actor" % "2.4.12",
  "com.typesafe.akka" %% "akka-http-core" % "3.0.0-RC1",
  "com.typesafe.akka" %% "akka-http" % "3.0.0-RC1",
  "com.typesafe.akka" %% "akka-http-xml" % "3.0.0-RC1",
  "de.heikoseeberger" %% "akka-http-json4s" % "1.5.3",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.json4s" %% "json4s-jackson" % "3.5.0"
)
    