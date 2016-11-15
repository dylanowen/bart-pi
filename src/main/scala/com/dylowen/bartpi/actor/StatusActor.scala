package com.dylowen.bartpi.actor

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

import scala.collection.concurrent.TrieMap

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object StatusActor extends SingletonActor {
  case object Read
  case class Write(update: Boolean = false, error: Boolean = false)

  final case class State(updateCount: Integer, apiErrors: Integer)

  override def props = Props(new StatusActor())
}
private class StatusActor extends Actor with ActorLogging {
  import StatusActor._

  var updateCount: Integer = 0
  var apiErrors: Integer = 0

  override def receive = {
    case Read => sender ! State(updateCount, apiErrors)
    case Write(true, _) =>
      this.updateCount += 1
    case Write(_, true) =>
      this.apiErrors += 1
    case error => log.error("Invalid Request: " + error)
  }
}
