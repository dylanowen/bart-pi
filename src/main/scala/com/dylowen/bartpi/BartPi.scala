package com.dylowen.bartpi

import akka.actor.ActorSystem
import com.dylowen.bartpi.utils.{ApplicationLifecycle, DefaultProperties}
import com.dylowen.bartpi.web.Server

import scala.io.StdIn


/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object BartPi {

  private implicit val system = ActorSystem("BartPi")

  def main(args: Array[String]): Unit = {
    val port = DefaultProperties.Main.getProperty("server.port").toInt

    // startup
    val lifecycle: Set[ApplicationLifecycle] = Set(
      new Server(port, system),
      new Scheduler
    )

    println("Press RETURN to stop...")
    StdIn.readLine() // let it run until user presses return

    // shutdown
    lifecycle.foreach(_.stop())
  }
}
