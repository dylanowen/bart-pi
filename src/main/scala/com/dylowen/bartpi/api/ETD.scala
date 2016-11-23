package com.dylowen.bartpi.api

import java.time.{Instant, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.util.Date

import scala.xml.NodeSeq

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object ETD {
  private val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a z")

  def apply(node: NodeSeq): ETD = {
    val datetime: Instant = ZonedDateTime.from(formatter.parse((node \ "date").text + " " + (node \ "time").text)).toInstant
    val departures: Seq[StationDeparture] = (node \ "station" \ "etd").map(StationDeparture.apply(_, datetime))

    new ETD(datetime, departures)
  }
}
class ETD(val dateTime: Instant, val departures: Seq[StationDeparture]) {
}
