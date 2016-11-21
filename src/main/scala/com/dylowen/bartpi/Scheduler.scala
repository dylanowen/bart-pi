package com.dylowen.bartpi

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Cancellable}
import com.dylowen.bartpi.actor.BartApiActor
import com.dylowen.bartpi.actor.BartApiActor._
import com.dylowen.bartpi.utils.ApplicationLifecycle

import scala.concurrent.duration.Duration

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
class Scheduler()(private implicit val system: ActorSystem) extends ApplicationLifecycle {
  private implicit val executionContext = system.dispatcher

  private val updateBartActor = system.actorOf(BartApiActor.props)
  private var cancellable: Option[Cancellable] = None

  def start(): Unit = {
    stop()
    this.cancellable = Some(this.system.scheduler.schedule(
      Duration.Zero,
      Duration.create(100000, TimeUnit.SECONDS),
      updateBartActor,
      Departure()
    ))
  }

  def stop(): Unit = {
    this.cancellable.foreach(_.cancel())
  }

  start()
}
