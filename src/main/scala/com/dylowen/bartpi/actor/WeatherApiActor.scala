package com.dylowen.bartpi.actor

import javax.swing.JList

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.dylowen.bartpi.utils.Properties
import com.fasterxml.jackson.databind.JsonMappingException
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.JsonAST.JValue
import org.json4s.ParserUtil.ParseException
import org.json4s.{DefaultFormats, _}
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import com.dylowen.bartpi.utils.JsonUtils._

import scala.util.Try

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object WeatherApiActor {
  private val WEATHER_HOST: String = Properties.get("weather.api.host")
  private val WEATHER_KEY: String = Properties.get("weather.api.key")

  case object WeatherByZip

  def props: Props = Props(new WeatherApiActor())
}
class WeatherApiActor extends Actor with ActorLogging {
  import WeatherApiActor._
  import context.dispatcher

  private implicit val system: ActorSystem = context.system
  private implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  private val http = Http(context.system)

  implicit val serialization = Serialization
  implicit val formats = DefaultFormats
  implicit val unmarshaller = Json4sSupport.json4sUnmarshaller[JValue]

  private val displayActor: ActorRef = ScrollingDisplayActor.get

  override def receive: Actor.Receive = {
    case WeatherByZip =>
      val latLng: String = Properties.get("weather.lat-lng")

      http.singleRequest(buildWeatherByLatLngRequest(latLng)).map({
        case HttpResponse(StatusCodes.OK, _, entity, _) =>
          Unmarshal(entity).to[JValue].map((node) => {
            val temp: Int = (node \ "currently" \ "temperature").toInt.toInt
            val rainChance: Int = ((node \ "daily" \ "data" \ "precipProbability").toList.head.toDecimal * 100).toInt

            var message = temp + "°"
            if (rainChance > 0) {
              message += " rain:" + rainChance + "%"
            }

            displayActor ! ScrollingDisplayActor.DisplayMessage(message, repeat = true)
          }).recover({
            case e => handleError("Failed to parse response", Some(e))
          })
        case HttpResponse(code, _, _, _) =>
          handleError("Request failed, response code: " + code)
      })
    case any => log.error("Invalid request: " + any)
  }

  private def handleError(errorMessage: String, t: Option[Throwable] = None): Unit = {
    displayActor ! ScrollingDisplayActor.DisplayMessage(errorMessage)

    if (t.isDefined) {
      log.error(t.get, errorMessage)
    }
    else {
      log.error(errorMessage)
    }
  }

  private def buildWeatherByLatLngRequest(latLng: String): HttpRequest = {
    val uri = Uri(WEATHER_HOST + "forecast/" + WEATHER_KEY + "/" + latLng)
    val request = HttpRequest(uri = uri)

    log.debug(request.toString())

    request
  }
}
