package com.dylowen.bartpi

import akka.actor.ActorSystem
import com.dylowen.bartpi.pi.Max7219
import com.dylowen.bartpi.utils.{ApplicationLifecycle, Properties}
import com.dylowen.bartpi.web.Server

import scala.io.StdIn


/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object BartPi {

  private lazy implicit val system = ActorSystem("BartPi")

  def main(args: Array[String]): Unit = {
    val host: String = Properties.get("server.host")
    val port: Int = Properties.get("server.port").toInt

    // startup
    val lifecycle: Set[ApplicationLifecycle] = Set(
      new Max7219(4)
      //new Server(host, port),
      //new Scheduler()
    )

    println("Press RETURN to stop...")
    StdIn.readLine() // let it run until user presses return

    // shutdown
    lifecycle.foreach(_.stop())
  }
}
