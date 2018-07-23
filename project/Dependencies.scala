import sbt._

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Jul-2018
  */
object Dependencies {

  object Versions {
    val Akka: String = "2.5.14"

    val Sttp: String = "1.2.3"

    val Circe: String = "0.9.3"

    val Pi4J: String = "1.1"
  }

  lazy val Akka: ModuleID = "com.typesafe.akka" %% "akka-actor" % Versions.Akka

  lazy val Sttp: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp" %% "core" % Versions.Sttp,
    "com.softwaremill.sttp" %% "async-http-client-backend-future" % Versions.Sttp,
    "com.softwaremill.sttp" %% "circe" % Versions.Sttp
  )

  lazy val Circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core" % Versions.Circe,
    "io.circe" %% "circe-generic" % Versions.Circe,
    "io.circe" %% "circe-parser" % Versions.Circe
  )

  lazy val Pi4J: ModuleID = "com.pi4j" % "pi4j-core" % Versions.Pi4J
}