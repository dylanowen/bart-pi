package com.dylowen.bartpi.api

import scala.xml.{Node, NodeSeq}

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object StationDeparture {
  def apply(xml: Node): StationDeparture = {
    val destination: StationDefinition = StationDefinitions.getByString((xml \ "abbreviation").text).get

    new StationDeparture(destination)
  }
}
class StationDeparture(private val destination: StationDefinition) {
}
