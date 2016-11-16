package com.dylowen.bartpi.actor

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.dylowen.bartpi.api.StationDefinition
import com.dylowen.bartpi.utils.DefaultProperties

import scala.xml.NodeSeq

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
    case HttpResponse(StatusCodes.OK, _, entity, _) =>
      Unmarshal(entity).to[NodeSeq].map((node) => {
        if (!findError(node)) {
          handleDeaparturesResponse(node)
        }
      })
    case HttpResponse(code, _, _, _) =>
      handleError("Request failed, response code: " + code)
    case _ =>
      handleError("Invalid request")
  }

  private def buildDeparturesRequest(origin: StationDefinition): HttpRequest = {
    buildRequest("etd", Query("cmd" -> "etd", "orig" -> origin.abbr))
  }

  private def handleDeaparturesResponse(node: NodeSeq): Unit = {
    val stations = node \ "station"


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
  private val BART_HOST: String = DefaultProperties.Main.getProperty("bart.api.host")
  private val BART_KEY: String = DefaultProperties.Main.getProperty("bart.api.key")

  case class Departure(station: StationDefinition)

  def props: Props = Props(new BartApiActor())
}