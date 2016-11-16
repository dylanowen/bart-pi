package com.dylowen.bartpi.actor

import java.util.Properties

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.dylowen.bartpi.api._
import com.dylowen.bartpi.utils.DefaultProperties

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object SettingsActor extends SingletonActor {
  case class SetStation(station: StationDefinition)
  case class SetLines(lines: Set[Line])
  case class SetDirection(direction: Direction)

  case class RouteStation(actor: ActorRef, messageBuilder: (StationDefinition) => Any)
  case class RouteLines(actor: ActorRef, messageBuilder: (Set[Line]) => Any)
  case class RouteDirection(actor: ActorRef, messageBuilder: (Direction) => Any)

  final case class Settings(station: StationDefinition, lines: Set[Line], direction: Direction)
  case object Get
  case class Put(settings: Settings) // TODO how can I serialize into this object?

  override def props = Props(new SettingsActor())
}
private class SettingsActor extends Actor with ActorLogging {
  import SettingsActor._

  private val properties: Properties = DefaultProperties.Main
  private var station: StationDefinition = StationDefinitions.getByString(properties.getProperty("bart.station"))
    .getOrElse(StationDefinitions.`12TH`)
  private var lines: Set[Line] = Lines.parseLines(properties.getProperty("bart.direction")) match {
    case Right(parsedLines) => parsedLines
    case Left(error) => log.error(error.message)
      Set.empty
  }
  private var direction: Direction = Directions.getByString(properties.getProperty("bart.direction")).getOrElse(Directions.SOUTH)

  override def receive: Receive = {
    case Get => sender ! Settings(station, lines, direction)
    case SetStation(newStation) => this.station = newStation
    case RouteStation(actor, messageBuilder) => actor ! messageBuilder.apply(station)
    case SetLines(newLines) => this.lines = newLines
    case RouteLines(actor, messageBuilder) => actor ! messageBuilder.apply(lines)
    case SetDirection(newDirection) => this.direction = newDirection
    case RouteDirection(actor, messageBuilder) => actor ! messageBuilder.apply(direction)
    case _ => log.error("Invalid message")
  }
}
