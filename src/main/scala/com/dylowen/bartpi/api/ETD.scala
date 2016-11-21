package com.dylowen.bartpi.api

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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
    val datetime = ZonedDateTime.from(formatter.parse((node \ "date").text + " " + (node \ "time").text))
    val departures: Seq[StationDeparture] = (node \ "station" \ "etd").map(StationDeparture.apply)

    new ETD(datetime, departures)
  }
}
class ETD(val dateTime: ZonedDateTime, val departures: Seq[StationDeparture]) {
}
