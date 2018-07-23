package com.dylowen.bartpi.bart

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.dylowen.bartpi.DisplayActor
import com.dylowen.bartpi.utils.Properties
import com.softwaremill.sttp._
import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend
import com.softwaremill.sttp.circe._
import io.circe.generic.auto._

import scala.collection.immutable
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object BartApiActor {
  private val BartHost: String = Properties.get("bart.api.host")
  private val BartKey: String = Properties.get("bart.api.key")

  case object Tick

  def props(display: ActorRef): Props = Props(new BartApiActor(display))

  private case class StationEstimate(station: String, departure: Instant)

}

class BartApiActor(display: ActorRef) extends Actor with ActorLogging {

  import BartApiActor._
  import context.dispatcher

  private implicit val backend: SttpBackend[Future, Nothing] = AsyncHttpClientFutureBackend()
  private implicit val system: ActorSystem = context.system

  private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a z")

  private val origin: String = Properties.get("bart.station").toUpperCase
  private val direction: Option[String] = Option(Properties.get("bart.direction")).map(_.toLowerCase)
  private val lines: immutable.Seq[String] = Properties.get("bart.lines").toLowerCase.split(',').to[immutable.Seq]
  private val timeThreshold: (Int, Int) = Properties.get("bart.time.threshold").split('-')
    .map(_.toInt) match {
    case Array(min, max) => (min, max)
    case Array(min) => (min, Int.MaxValue)
  }

  override def receive: Actor.Receive = {
    case Tick => {
      departuresRequest
        .send()
        .onComplete({
          case Success(response) => response.body match {
            case Right(body) => body match {
              case Right(bartRoot) => handleResponse(bartRoot)
              case Left(error) => handleError("Parse error", Some(error))
            }
            case Left(errorMessage) => handleError("Request error: " + errorMessage)
          }
          case Failure(exception) => handleError("Internal client error", Some(exception))
        })
    }
    case any => log.error("Invalid request: " + any)
  }

  private def handleResponse(top: BartRoot): Unit = {
    val root: BartResponse = top.root
    val requestTime: Instant = Instant.from(formatter.parse(root.date + " " + root.time))
    val minTime: Instant = requestTime.plus(timeThreshold._1, ChronoUnit.MINUTES)
    val maxTime: Instant = requestTime.plus(timeThreshold._2, ChronoUnit.MINUTES)
    val station: Station = root.station.head

    val departureString: String = station.etd
      .flatMap((etd: Etd) => {
        val abbr: String = etd.abbreviation

        etd.estimate
          .filter((estimate: Estimate) => {
            // filter by lines
            lines.contains(estimate.color.toLowerCase)
          })
          .map((estimate: Estimate) => {

            // minutes can also be "leaving" so ignore it in that case
            val minutes: Int = Try(estimate.minutes.toInt).getOrElse(0)
            val departure: Instant = requestTime.plus(minutes, ChronoUnit.MINUTES)

            // create a station estimate
            StationEstimate(abbr, departure)
          })
      })
      .filter(_.departure.compareTo(minTime) >= 0)
      .filter(_.departure.compareTo(maxTime) <= 0)
      .sortBy(_.departure)
      .map((estimate: StationEstimate) => {
        estimate.station + ":" + requestTime.until(estimate.departure, ChronoUnit.MINUTES)
      })
      .mkString(" ")

    display ! DisplayActor.DisplayMessage(departureString, repeat = true)
  }

  private val departuresRequest: RequestT[Id, Either[io.circe.Error, BartRoot], Nothing] = {
    val rawUri: String = s"${BartHost}etd.aspx?" +
      s"json=y" +
      s"&key=$BartKey" +
      s"&cmd=etd" +
      s"&orig=$origin" +
      direction.map((dir: String) => s"&dir=$dir").getOrElse("")

    log.debug(s"GET $rawUri")

    sttp.get(uri"$rawUri")
      .response(asJson[BartRoot])
  }

  private def handleError(errorMessage: String, t: Option[Throwable] = None): Unit = {
    // tell our status to track an error
    display ! DisplayActor.DisplayMessage(errorMessage.substring(0, 40))

    if (t.isDefined) {
      log.error(t.get, errorMessage)
    }
    else {
      log.error(errorMessage)
    }
  }
}

