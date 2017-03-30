package com.dylowen.bartpi.api

import com.dylowen.bartpi.utils.EnumBuilder

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
class StationDefinition(val abbr: String, val name: String)
object StationDefinitions extends EnumBuilder[StationDefinition] {
  private def register(station: StationDefinition): StationDefinition = register(station.abbr, station)

  val `12TH` = register(new StationDefinition("12TH", "12th St. Oakland City Center"))
  val `16TH` = register(new StationDefinition("16TH", "16th St. Mission"))
  val `19TH` = register(new StationDefinition("19TH", "19th St. Oakland"))
  val `24TH` = register(new StationDefinition("24TH", "24th St. Mission"))
  val ASHB = register(new StationDefinition("ASHB", "Ashby"))
  val BALB = register(new StationDefinition("BALB", "Balboa Park"))
  val BAYF = register(new StationDefinition("BAYF", "Bay Fair"))
  val CAST = register(new StationDefinition("CAST", "Castro Valley"))
  val CIVC = register(new StationDefinition("CIVC", "Civic Center/UN Plaza"))
  val COLS = register(new StationDefinition("COLS", "Coliseum"))
  val COLM = register(new StationDefinition("COLM", "Colma"))
  val CONC = register(new StationDefinition("CONC", "Concord"))
  val DALY = register(new StationDefinition("DALY", "Daly City"))
  val DBRK = register(new StationDefinition("DBRK", "Downtown Berkeley"))
  val DUBL = register(new StationDefinition("DUBL", "Dublin/Pleasanton"))
  val DELN = register(new StationDefinition("DELN", "El Cerrito del Norte"))
  val PLZA = register(new StationDefinition("PLZA", "El Cerrito Plaza"))
  val EMBR = register(new StationDefinition("EMBR", "Embarcadero"))
  val FRMT = register(new StationDefinition("FRMT", "Fremont"))
  val FTVL = register(new StationDefinition("FTVL", "Fruitvale"))
  val GLEN = register(new StationDefinition("GLEN", "Glen Park"))
  val HAYW = register(new StationDefinition("HAYW", "Hayward"))
  val LAFY = register(new StationDefinition("LAFY", "Lafayette"))
  val LAKE = register(new StationDefinition("LAKE", "Lake Merritt"))
  val MCAR = register(new StationDefinition("MCAR", "MacArthur"))
  val MLBR = register(new StationDefinition("MLBR", "Millbrae"))
  val MONT = register(new StationDefinition("MONT", "Montgomery St."))
  val NBRK = register(new StationDefinition("NBRK", "North Berkeley"))
  val NCON = register(new StationDefinition("NCON", "North Concord/Martinez"))
  val OAKL = register(new StationDefinition("OAKL", "Oakland Int'l Airport"))
  val ORIN = register(new StationDefinition("ORIN", "Orinda"))
  val PITT = register(new StationDefinition("PITT", "Pittsburg/Bay Point"))
  val PHIL = register(new StationDefinition("PHIL", "Pleasant Hill/Contra Costa Centre"))
  val POWL = register(new StationDefinition("POWL", "Powell St."))
  val RICH = register(new StationDefinition("RICH", "Richmond"))
  val ROCK = register(new StationDefinition("ROCK", "Rockridge"))
  val SBRN = register(new StationDefinition("SBRN", "San Bruno"))
  val SFIA = register(new StationDefinition("SFIA", "San Francisco Int'l Airport"))
  val SANL = register(new StationDefinition("SANL", "San Leandro"))
  val SHAY = register(new StationDefinition("SHAY", "South Hayward"))
  val SSAN = register(new StationDefinition("SSAN", "South San Francisco"))
  val UCTY = register(new StationDefinition("UCTY", "Union City"))
  val WARM = register(new StationDefinition("WARM", "Warm Springs"))
  val WCRK = register(new StationDefinition("WCRK", "Walnut Creek"))
  val WDUB = register(new StationDefinition("WDUB", "West Dublin/Pleasanton"))
  val WOAK = register(new StationDefinition("WOAK", "West Oakland"))

  val UNKNOWN = new StationDefinition("UNKN", "Unknown")

  build()
}
