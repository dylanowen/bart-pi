package com.dylowen.bartpi.api

import java.time.Instant
import java.time.temporal.{ChronoUnit, TemporalUnit}

import scala.util.Try
import scala.xml.{Node, NodeSeq}

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object StationDeparture {
  def apply(xml: Node, requestDateTime: Instant): StationDeparture = {
    val destination: StationDefinition = StationDefinitions.getByString((xml \ "abbreviation").text)
      .getOrElse(StationDefinitions.UNKNOWN)
    val estimates: Seq[Estimate] = (xml \ "estimate").map(Estimate.apply(_, requestDateTime))

    new StationDeparture(destination, estimates)
  }
}
class StationDeparture(val destination: StationDefinition, val estimates: Seq[Estimate]) {
}

object Estimate {
  def apply(xml: Node, requestDateTime: Instant): Estimate = {
    // minutes can also be "leaving" so ignore it in that case
    val minutes: Int = Try((xml \ "minutes").text.toInt).getOrElse(0)
    val departureTime: Instant = requestDateTime.plus(minutes, ChronoUnit.MINUTES)
    val line: Line = Lines.getByString((xml \ "color").text).getOrElse(Lines.UNKNOWN)
    val direction: Direction = Directions.getByString((xml \ "direction").text).getOrElse(Directions.UNKNOWN)

    new Estimate(departureTime, line, direction)
  }
}
class Estimate(val departureTime: Instant, val line: Line, val direction: Direction)
