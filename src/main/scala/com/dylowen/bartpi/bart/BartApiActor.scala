package com.dylowen.bartpi.bart

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

import akka.actor.{Actor, ActorRef, ActorLogging, ActorSystem, Props}
import com.dylowen.bartpi.DisplayActor
import com.dylowen.bartpi.utils.Properties
import com.softwaremill.sttp._
import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend
import com.softwaremill.sttp.circe._
import io.circe.generic.auto._

import scala.collection.immutable
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object BartApiActor {
  private val BartHost: String = Properties().getUnsafe[String]("bart-pi.api.host")
  private val BartKey: String = Properties().getUnsafe[String]("bart-pi.api.key")

  case object Tick
  private case object Open

  def props(display: ActorRef): Props = Props(new BartApiActor(display))

  private case class StationEstimate(station: String, departure: Instant)

}

class BartApiActor(display: ActorRef) extends Actor with ActorLogging {

  import BartApiActor._
  import context.dispatcher

  private implicit val backend: SttpBackend[Future, Nothing] = AsyncHttpClientFutureBackend()
  private implicit val system: ActorSystem = context.system

  private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a z")

  private val origin: String = Properties().getUnsafe[String]("bart-pi.station").toUpperCase
  private val direction: Option[String] = Properties().get[String]("bart-pi.direction").map(_.toLowerCase)
  private val lines: immutable.Seq[String] = Properties().getUnsafe[immutable.Seq[String]]("bart-pi.lines").map(_.toLowerCase)
  private val timeThreshold: (FiniteDuration, FiniteDuration) = {
    Properties().getUnsafe[FiniteDuration]("bart-pi.time-threshold.start") ->
    Properties().getUnsafe[FiniteDuration]("bart-pi.time-threshold.end")
  }

  log.debug(s"Origin: $origin")
  log.debug(s"Direction: $direction")
  log.debug(s"Lines: $lines")
  log.debug(s"Time Threshold: $timeThreshold")

  override def receive: Actor.Receive = open

  private def open: Actor.Receive = {
    case Tick => {
      context.become(closed)
      val start: Long = System.nanoTime()

      val apiResponse: Future[Response[Either[io.circe.Error, BartRoot]]] = departuresRequest.send()

      // log our response and set ourselves open
      apiResponse.andThen({
        case _ => {
          val profiledTime: FiniteDuration = (System.nanoTime() - start) nanos

          log.debug(s"Request complete in: ${profiledTime.toMillis}ms")

          context.become(open)
        }
      })

      apiResponse.onComplete({
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

  private def closed: Actor.Receive = {
    case Open => context.become(open)
    case _ => log.warning("Not taking requests now")
  }

  private def handleResponse(top: BartRoot): Unit = {
    val root: BartResponse = top.root
    val requestTime: Instant = Instant.from(formatter.parse(root.date + " " + root.time))
    val minTime: Instant = Instant.now().plus(timeThreshold._1.toMillis, ChronoUnit.MILLIS)
    val maxTime: Instant = Instant.now().plus(timeThreshold._2.toMillis, ChronoUnit.MILLIS)
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
        estimate.station + ":" + Instant.now().until(estimate.departure, ChronoUnit.MINUTES)
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

    log.debug(s"Request URI: $rawUri")

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

