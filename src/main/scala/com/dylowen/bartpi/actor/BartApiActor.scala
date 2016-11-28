package com.dylowen.bartpi.actor

import java.time.Instant
import java.time.temporal.ChronoUnit

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.dylowen.bartpi.api._
import com.dylowen.bartpi.utils.Properties

import scala.util.Try
import scala.xml.NodeSeq

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object BartApiActor {
  private val BART_HOST: String = Properties.get("bart.api.host")
  private val BART_KEY: String = Properties.get("bart.api.key")

  case object Departure

  def props: Props = Props(new BartApiActor())
}
class BartApiActor extends Actor with ActorLogging {

  import BartApiActor._
  import context.dispatcher

  private implicit val system: ActorSystem = context.system
  private implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  private val statusActor: ActorRef = StatusActor.get
  private val displayActor: ActorRef = ScrollingDisplayActor.get
  private val http = Http(context.system)

  override def receive: Actor.Receive = {
    case Departure =>
      val station: StationDefinition = StationDefinitions.getByString(Properties.get("bart.station")).getOrElse(StationDefinitions.`12TH`)
      val direction: Option[Direction] = Directions.getByString(Properties.get("bart.direction"))

      http.singleRequest(buildDeparturesRequest(station, direction)).map({
        case HttpResponse(StatusCodes.OK, _, entity, _) =>
          Unmarshal(entity).to[NodeSeq].map((node) => {
            if (!findError(node)) {
              handleDeparturesResponse(node)
            }
          }).recover({
            case e => handleError("Failed to parse response", Some(e))
          })
        case HttpResponse(code, _, _, _) =>
          handleError("Request failed, response code: " + code)
      })
    case any => log.error("Invalid request: " + any)
  }

  private def buildDeparturesRequest(origin: StationDefinition, direction: Option[Direction] = None): HttpRequest = {
    var query: Query = Query("cmd" -> "etd", "orig" -> origin.abbr)
    direction.foreach(dir => query = query.+:("dir", dir.abbr))

    buildRequest("etd", query)
  }

  private def handleDeparturesResponse(node: NodeSeq): Unit = {
    // TODO this parsing / error handling is terrible
    val lines: Set[Line] = Lines.parseLines(Properties.get("bart.lines")) match {
      case Right(parsedLines) => parsedLines
      case Left(error) =>
        log.error(error.message)
        Set.empty
    }
    val direction: Direction = Directions.getByString(Properties.get("bart.direction")).get
    val timeThreshold: Int = Properties.get("bart.time.threshold").toInt

    //TODO this breaks with no data (in the middle of the night)
    // filter our departures
    val departures: Seq[StationDeparture] = ETD(node).departures
      .filter(_.estimates.nonEmpty)
      .filter(_.estimates.head.direction == direction)
      .filter(departure => lines.contains(departure.estimates.head.line))

    val now = Instant.now()
    val message = departures.flatMap(departure => {
        departure.estimates
          .filter(_.departureTime.minus(timeThreshold, ChronoUnit.MINUTES).compareTo(now) > 0)
          .map(estimate => (estimate.departureTime, departure.destination.abbr))
          .slice(0, 2) // just get 2 for each line
      })
      .sortBy(_._1)
      .map({ case (time, station) => station + ":" + now.until(time, ChronoUnit.MINUTES) })
      .mkString(" ")

    displayActor ! ScrollingDisplayActor.DisplayMessage(message, repeat = true)

    statusActor ! StatusActor.Write(update = true)
  }

  private def buildRequest(path: String, query: Query = Query.Empty): HttpRequest = {
    val finalQuery: Query = query.+:("key" -> BART_KEY)
    val uri = Uri(BART_HOST + path + ".aspx").withQuery(finalQuery)
    val request = HttpRequest(uri = uri)
    log.debug(request.toString())

    request
  }

  private def findError(node: NodeSeq): Boolean = {
    val error: NodeSeq = node \ "message" \ "error"
    if (error.length > 0) {
      // we found an error so do something
      handleError(error \ "details" toString())

      true
    }
    else {
      false
    }
  }

  private def handleError(errorMessage: String, t: Option[Throwable] = None): Unit = {
    // tell our status to track an error
    displayActor ! ScrollingDisplayActor.DisplayMessage(errorMessage)
    statusActor ! StatusActor.Write(error = true)
    if (t.isDefined) {
      log.error(t.get, errorMessage)
    }
    else {
      log.error(errorMessage)
    }
  }
}

