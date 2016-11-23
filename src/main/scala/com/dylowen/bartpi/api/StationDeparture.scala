package com.dylowen.bartpi.api

import java.time.Instant
import java.time.temporal.{ChronoUnit, TemporalUnit}

import scala.xml.{Node, NodeSeq}

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object StationDeparture {
  def apply(xml: Node, requestDateTime: Instant): StationDeparture = {
    val destination: StationDefinition = StationDefinitions.getByString((xml \ "abbreviation").text).get
    val estimates: Seq[Estimate] = (xml \ "estimate").map(Estimate.apply(_, requestDateTime))

    new StationDeparture(destination, estimates)
  }
}
class StationDeparture(val destination: StationDefinition, val estimates: Seq[Estimate]) {
}

object Estimate {
  def apply(xml: Node, requestDateTime: Instant): Estimate = {
    val minutes: Int = (xml \ "minutes").text.toInt
    val departureTime: Instant = requestDateTime.plus(minutes, ChronoUnit.MINUTES)
    val line: Line = Lines.getByString((xml \ "color").text).get
    val direction: Direction = Directions.getByString((xml \ "direction").text).get

    new Estimate(departureTime, line, direction)
  }
}
class Estimate(val departureTime: Instant, val line: Line, val direction: Direction)
