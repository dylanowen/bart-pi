package com.dylowen.bartpi.actor

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.dylowen.bartpi.api._
import com.dylowen.bartpi.utils.Properties

import scala.xml.NodeSeq

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
class BartApiActor extends Actor with ActorLogging {
  import BartApiActor._
  import context.dispatcher

  private implicit val system: ActorSystem = context.system
  private implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  private val bartActor: ActorRef = StatusActor.get
  private val http = Http(context.system)

  override def receive = {
    // if we receive an empty departure message fill in the defaults
    case Departure(None, _) =>
      val station: StationDefinition = StationDefinitions.getByString(Properties.get("bart.station")).getOrElse(StationDefinitions.`12TH`)
      val direction: Option[Direction] = Directions.getByString(Properties.get("bart.direction"))

      receive(Departure(Some(station), direction))
    case Departure(station, direction) =>
      // we know we've already matched on empty stations so always grab the value
      http.singleRequest(buildDeparturesRequest(station.get, direction)).map({
        case HttpResponse(StatusCodes.OK, _, entity, _) =>
          Unmarshal(entity).to[NodeSeq].map((node) => {
            if (!findError(node)) {
              handleDeaparturesResponse(node)
            }
          })
        case HttpResponse(code, _, _, _) =>
          handleError("Request failed, response code: " + code)
      })
    case _ =>
      handleError("Invalid request")
  }

  private def buildDeparturesRequest(origin: StationDefinition, direction: Option[Direction] = None): HttpRequest = {
    var query: Query = Query("cmd" -> "etd", "orig" -> origin.abbr)
    direction.foreach(dir => query = query.+:("dir", dir.abbr))

    buildRequest("etd", query)
  }

  private def handleDeaparturesResponse(node: NodeSeq): Unit = {
    val etd: ETD = ETD(node)

    bartActor ! StatusActor.Write(update = true)
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

  private def handleError(errorMessage: String): Unit = {
    // tell our status to track an error
    bartActor ! StatusActor.Write(error = true)
    log.error(errorMessage)
  }
}

object BartApiActor {
  private val BART_HOST: String = Properties.get("bart.api.host")
  private val BART_KEY: String = Properties.get("bart.api.key")

  case class Departure(station: Option[StationDefinition] = None, direction: Option[Direction] = None)

  def props: Props = Props(new BartApiActor())
}

