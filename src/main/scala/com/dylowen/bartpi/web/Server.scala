package com.dylowen.bartpi.web

import akka.actor.{ActorRef, ActorSystem}
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.dylowen.bartpi.actor.StatusActor
import com.dylowen.bartpi.actor.StatusActor.State
import com.dylowen.bartpi.utils.ApplicationLifecycle
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
class Server(private val host: String, private val port: Integer)(private implicit val system: ActorSystem) extends ApplicationLifecycle {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats
  implicit val marshaller = Json4sSupport.json4sMarshaller[State]

  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val executionContext: ExecutionContext = system.dispatcher
  private implicit val timeout: Timeout = Timeout(5.seconds)
  private val log: LoggingAdapter = system.log

  private val statusActor: ActorRef = StatusActor.get

  val route: Route = pathEndOrSingleSlash {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,
        """
          |<html>
          | <body>Hello world!</body>
          |</html>
        """.stripMargin))
    }
  } ~ path("status") {
    get {
      complete((statusActor ? StatusActor.Read).map[ToResponseMarshallable] {
        case state: State => state
      })
    }
  } /* ~ path("settings") {
    get {
      complete((settingsActor ? SettingsActor.Get).map[ToResponseMarshallable] {
        case settings: SettingsActor.Settings => settings
      })
    }
  }*/

  val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(route, host, port)
  bindingFuture.onComplete({
    case Success(_) => log.info("Server running on port: " + port)
    case Failure(e) => log.error(e, "Failed to bind to {}:{}!", host, port)
  })

  def stop(): Unit = {
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate())
  }
}
