package com.dylowen.bartpi.web

import akka.actor.{ActorRef, ActorSystem}
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.dylowen.bartpi.actor.StatusActor.State
import com.dylowen.bartpi.actor.{SettingsActor, StatusActor}
import com.dylowen.bartpi.utils.ApplicationLifecycle
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
class Server(private val port: Integer, private implicit val system: ActorSystem) extends ApplicationLifecycle {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats
  implicit val marshaller = Json4sSupport.json4sMarshaller[State]

  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val executionContext: ExecutionContext = system.dispatcher
  private implicit val timeout: Timeout = Timeout(5.seconds)
  private val log: LoggingAdapter = system.log

  private val statusActor: ActorRef = StatusActor.get
  private val settingsActor: ActorRef = SettingsActor.get

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

  val bindingFuture = Http().bindAndHandle(route, "localhost", port)
  println("Server running on port: " + port)

  def stop(): Unit = {
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate())
  }
}
