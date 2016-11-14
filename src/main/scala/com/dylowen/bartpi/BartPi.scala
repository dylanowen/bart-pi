package com.dylowen.bartpi

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpEntity, _}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.dylowen.bartpi.actor.StatusActor
import com.dylowen.bartpi.actor.StatusActor.State
import org.json4s.{DefaultFormats, jackson}
import de.heikoseeberger.akkahttpjson4s._

import scala.concurrent.duration._
import scala.io.StdIn


/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object BartPi {

  private implicit val system = ActorSystem("BartPi")
  private implicit val materializer = ActorMaterializer()
  private implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(5.seconds)

  private val bartActor: ActorRef = StatusActor.get

  def main(args: Array[String]): Unit = {
    val bartApiKey = Properties.Main.getProperty("bart.api.key")
    println("Bart Api Key: " + bartApiKey)

    val shutdownFunctions: Set[() => Unit] = Set(
      setupServer(),
      setupScheduler()
    )

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return

    shutdownFunctions.foreach(_.apply())
  }

  private def setupServer(): () => Unit = {
    implicit val serialization = jackson.Serialization
    implicit val formats = DefaultFormats
    implicit val marshaller = Json4sSupport.json4sMarshaller[State]

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
       complete((bartActor ? StatusActor.Read).map[ToResponseMarshallable] {
         case state: State => state
       })
      }
    }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    () => {
      bindingFuture
        .flatMap(_.unbind()) // trigger unbinding from the port
        .onComplete(_ => system.terminate())

      println("Shutdown Server")
    } // and shutdown when done
  }

  private def setupScheduler(): () => Unit = {
    val scheduler = new Scheduler
    scheduler.start()

    () => {
      scheduler.stop()
      println("Shutdown Scheduler")
    }
  }
}
