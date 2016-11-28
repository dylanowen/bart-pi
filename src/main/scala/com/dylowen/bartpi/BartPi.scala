package com.dylowen.bartpi

import java.util.Locale

import akka.actor.ActorSystem
import com.dylowen.bartpi.actor.ScrollingDisplayActor.RegisterMax
import com.dylowen.bartpi.actor.{ScrollingDisplayActor, ScrollingDisplayActor$}
import com.dylowen.bartpi.pi.{DefaultFont8x6, Max7219}
import com.dylowen.bartpi.utils.{ApplicationLifecycle, Properties}
import com.dylowen.bartpi.web.Server
import com.pi4j.io.spi.SpiChannel

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
    val host: String = Properties.get("server.host")
    val port: Int = Properties.get("server.port").toInt

    // set default locale to en_us for date parsing
    Locale.setDefault(Locale.US)

    // this kind of sucks for keeping track of this
    val max: Max7219 = new Max7219(SpiChannel.CS0, 4)
    val display = ScrollingDisplayActor.get
    display ! RegisterMax(max)

    // startup
    val lifecycle: Set[ApplicationLifecycle] = Set(
      max,
      new Server(host, port),
      new Scheduler()
    )

    println("Press RETURN to stop...")
    StdIn.readLine() // let it run until user presses return

    // shutdown
    lifecycle.foreach(_.stop())
  }
}
