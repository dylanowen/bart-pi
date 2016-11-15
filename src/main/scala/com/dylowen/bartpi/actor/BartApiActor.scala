package com.dylowen.bartpi.actor

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.dylowen.bartpi.Properties

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
class BartApiActor extends Actor with ActorLogging {
  import BartApiActor._
  import akka.pattern.pipe
  import context.dispatcher

  private implicit val system: ActorSystem = context.system
  private implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  private val bartActor: ActorRef = StatusActor.get
  private val http = Http(context.system)

  override def receive = {
    case Departure(station) =>
      http.singleRequest(buildDeparturesRequest(station)).pipeTo(self)
    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      log.debug(entity.toString)

      bartActor ! StatusActor.Write(update = true)
    case HttpResponse(code, _, _, _) =>
      log.error("Request failed, response code: " + code)
      bartActor ! StatusActor.Write(error = true)
    case _ =>
      log.error("Invalid request")
  }

  private def buildDeparturesRequest(origin: Station): HttpRequest = {
    buildRequest("etd", Query("cmd" -> "etd", "orig" -> origin.name))
  }

  private def buildRequest(path: String, query: Query = Query.Empty): HttpRequest = {
    val finalQuery: Query = query.+:("key" -> BART_KEY)
    val uri = Uri(BART_HOST + path + ".aspx").withQuery(finalQuery)
    val request = HttpRequest(uri = uri)
    log.debug(request.toString())

    request
  }
}

object BartApiActor {
  private val BART_HOST: String = Properties.Main.getProperty("bart.api.host")
  private val BART_KEY: String = Properties.Main.getProperty("bart.api.key")

  case class Departure(station: Station)

  sealed abstract class Station(val abbr: String, val name: String)
  case object `12TH` extends Station("12TH", "12th St. Oakland City Center")
  case object `16TH` extends Station("16TH", "16th St. Mission")
  case object `19TH` extends Station("19TH", "19th St. Oakland")
  case object `24TH` extends Station("24TH", "24th St. Mission")
  case object `ASHB` extends Station("ASHB", "Ashby")
  case object `BALB` extends Station("BALB", "Balboa Park")
  case object `BAYF` extends Station("BAYF", "Bay Fair")
  case object `CAST` extends Station("CAST", "Castro Valley")
  case object `CIVC` extends Station("CIVC", "Civic Center/UN Plaza")
  case object `COLS` extends Station("COLS", "Coliseum")
  case object `COLM` extends Station("COLM", "Colma")
  case object `CONC` extends Station("CONC", "Concord")
  case object `DALY` extends Station("DALY", "Daly City")
  case object `DBRK` extends Station("DBRK", "Downtown Berkeley")
  case object `DUBL` extends Station("DUBL", "Dublin/Pleasanton")
  case object `DELN` extends Station("DELN", "El Cerrito del Norte")
  case object `PLZA` extends Station("PLZA", "El Cerrito Plaza")
  case object `EMBR` extends Station("EMBR", "Embarcadero")
  case object `FRMT` extends Station("FRMT", "Fremont")
  case object `FTVL` extends Station("FTVL", "Fruitvale")
  case object `GLEN` extends Station("GLEN", "Glen Park")
  case object `HAYW` extends Station("HAYW", "Hayward")
  case object `LAFY` extends Station("LAFY", "Lafayette")
  case object `LAKE` extends Station("LAKE", "Lake Merritt")
  case object `MCAR` extends Station("MCAR", "MacArthur")
  case object `MLBR` extends Station("MLBR", "Millbrae")
  case object `MONT` extends Station("MONT", "Montgomery St.")
  case object `NBRK` extends Station("NBRK", "North Berkeley")
  case object `NCON` extends Station("NCON", "North Concord/Martinez")
  case object `OAKL` extends Station("OAKL", "Oakland Int'l Airport")
  case object `ORIN` extends Station("ORIN", "Orinda")
  case object `PITT` extends Station("PITT", "Pittsburg/Bay Point")
  case object `PHIL` extends Station("PHIL", "Pleasant Hill/Contra Costa Centre")
  case object `POWL` extends Station("POWL", "Powell St.")
  case object `RICH` extends Station("RICH", "Richmond")
  case object `ROCK` extends Station("ROCK", "Rockridge")
  case object `SBRN` extends Station("SBRN", "San Bruno")
  case object `SFIA` extends Station("SFIA", "San Francisco Int'l Airport")
  case object `SANL` extends Station("SANL", "San Leandro")
  case object `SHAY` extends Station("SHAY", "South Hayward")
  case object `SSAN` extends Station("SSAN", "South San Francisco")
  case object `UCTY` extends Station("UCTY", "Union City")
  case object `WCRK` extends Station("WCRK", "Walnut Creek")
  case object `WDUB` extends Station("WDUB", "West Dublin/Pleasanton")
  case object `WOAK` extends Station("WOAK", "West Oakland")

  def props: Props = Props(new BartApiActor())
}