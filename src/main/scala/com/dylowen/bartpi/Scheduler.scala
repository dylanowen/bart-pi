package com.dylowen.bartpi

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Cancellable, Props}

import scala.concurrent.duration.Duration

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
class Scheduler(private val bartActor: ActorRef, private implicit val system: ActorSystem) {
  private implicit val executionContext = system.dispatcher

  private val updateBartActor = this.system.actorOf(UpdateBartActor.props(bartActor))
  private var cancellable: Option[Cancellable] = None

  def start(): Unit = {
    stop()
    this.cancellable = Some(this.system.scheduler.schedule(
      Duration.Zero,
      Duration.create(1, TimeUnit.SECONDS),
      updateBartActor,
      Nil
    ))
  }

  def stop(): Unit = {
    this.cancellable.foreach(_.cancel())
  }
}
