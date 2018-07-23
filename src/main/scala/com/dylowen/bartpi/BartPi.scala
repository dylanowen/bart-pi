package com.dylowen.bartpi

import java.util.Locale

import akka.actor.{ActorRef, ActorSystem, Cancellable}
import com.dylowen.bartpi.bart.BartApiActor
import com.dylowen.bartpi.pi.Max7219
import com.pi4j.io.spi.SpiChannel

import scala.concurrent.duration._
import scala.io.StdIn
import scala.language.postfixOps


/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object BartPi {

  val DisplayTick: FiniteDuration = 60 milliseconds
  val BartUpdate: FiniteDuration = 30 seconds

  private implicit val system: ActorSystem = ActorSystem("BartPi")

  def main(args: Array[String]): Unit = {
    // set default locale to en_us for date parsing
    Locale.setDefault(Locale.US)

    val max: Max7219 = new Max7219(SpiChannel.CS0, 4)

    // startup
    val cancelMain: () => Unit = mainLoop(max)

    println("Press RETURN to stop...")
    StdIn.readLine() // let it run until user presses return

    // shutdown
    cancelMain()
    max.stop()
  }

  private def mainLoop(max: Max7219)(implicit system: ActorSystem): () => Unit = {

    import system.dispatcher

    val displayActor: ActorRef = system.actorOf(DisplayActor.props(max))
    val bartApiActor: ActorRef = system.actorOf(BartApiActor.props(displayActor))


    val cancellables: Seq[Cancellable] = Seq(
      system.scheduler.schedule(
        Duration.Zero,
        DisplayTick,
        displayActor,
        DisplayActor.Tick
      ),
      system.scheduler.schedule(
        Duration.Zero,
        BartUpdate,
        bartApiActor,
        BartApiActor.Tick
      )
    )

    () => {
      cancellables.foreach(_.cancel())
    }
  }
}
