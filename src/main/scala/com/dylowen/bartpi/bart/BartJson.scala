package com.dylowen.bartpi.bart

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Jul-2018
  */
sealed case class BartRoot(root: BartResponse)

sealed case class BartResponse(date: String, time: String, station: Seq[Station], message: String)

sealed case class Station(name: String, abbr: String, etd: Seq[Etd])

sealed case class Etd(destination: String, abbreviation: String, estimate: Seq[Estimate])

sealed case class Estimate(minutes: String, direction: String, color: String)