package com.dylowen.bartpi

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Cancellable}
import com.dylowen.bartpi.actor.{BartApiActor, ScrollingDisplayActor}
import com.dylowen.bartpi.utils.ApplicationLifecycle

import scala.concurrent.duration.Duration

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object Scheduler {
  val BART_UPDATE_FREQUENCY = 30
  val DISPLAY_TICK = 75
}
class Scheduler()(private implicit val system: ActorSystem) extends ApplicationLifecycle {

  import Scheduler._

  private implicit val executionContext = system.dispatcher

  private val updateBartActor = system.actorOf(BartApiActor.props)
  private val scrollingActor = ScrollingDisplayActor.get
  private var bartUpdater: Option[Cancellable] = None
  private var screenUpdater: Option[Cancellable] = None

  def start(): Unit = {
    stop()
    this.bartUpdater = Some(this.system.scheduler.schedule(
      Duration.Zero,
      Duration.create(BART_UPDATE_FREQUENCY, TimeUnit.SECONDS),
      updateBartActor,
      BartApiActor.Departure
    ))
    this.screenUpdater = Some(this.system.scheduler.schedule(
      Duration.Zero,
      Duration.create(DISPLAY_TICK, TimeUnit.MILLISECONDS),
      scrollingActor,
      ScrollingDisplayActor.Tick
    ))
  }

  def stop(): Unit = {
    this.bartUpdater.foreach(_.cancel())
    this.screenUpdater.foreach(_.cancel())
  }

  start()
}
