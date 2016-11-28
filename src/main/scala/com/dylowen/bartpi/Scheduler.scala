package com.dylowen.bartpi

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Cancellable}
import com.dylowen.bartpi.actor.{BartApiActor, ScrollingDisplayActor, WeatherApiActor}
import com.dylowen.bartpi.utils.ApplicationLifecycle

import scala.concurrent.duration.{Duration, FiniteDuration}

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object Scheduler {
  val DISPLAY_TICK: FiniteDuration = Duration.create(75, TimeUnit.MILLISECONDS)
  val BART_UPDATE: FiniteDuration = Duration.create(30, TimeUnit.SECONDS)
  val WEATHER_UPDATE: FiniteDuration = Duration.create(30, TimeUnit.MINUTES)
}
class Scheduler()(private implicit val system: ActorSystem) extends ApplicationLifecycle {

  import Scheduler._

  private implicit val executionContext = system.dispatcher

  private val bartApiActor = system.actorOf(BartApiActor.props)
  private val weatherApiActor = system.actorOf(WeatherApiActor.props)
  private val scrollingActor = ScrollingDisplayActor.get
  private var screenUpdater: Option[Cancellable] = None
  private var bartUpdater: Option[Cancellable] = None
  private var weatherUpdater: Option[Cancellable] = None

  def start(): Unit = {
    stop()
    this.screenUpdater = Some(this.system.scheduler.schedule(
      Duration.Zero,
      DISPLAY_TICK,
      scrollingActor,
      ScrollingDisplayActor.Tick
    ))
    this.bartUpdater = Some(this.system.scheduler.schedule(
      Duration.Zero,
      BART_UPDATE,
      bartApiActor,
      BartApiActor.Departure
    ))
    this.weatherUpdater = Some(this.system.scheduler.schedule(
      Duration.Zero,
      WEATHER_UPDATE,
      weatherApiActor,
      WeatherApiActor.WeatherByZip
    ))
  }

  def stop(): Unit = {
    this.screenUpdater.foreach(_.cancel())
    this.bartUpdater.foreach(_.cancel())
    this.weatherUpdater.foreach(_.cancel())
  }

  start()
}
