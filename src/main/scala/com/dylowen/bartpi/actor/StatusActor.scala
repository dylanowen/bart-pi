package com.dylowen.bartpi.actor

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.collection.concurrent.TrieMap

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object StatusActor extends SingletonActor {
  case object Read
  case class Write()

  final case class State(updateCount: Integer = 0)

  override def props = Props(new StatusActor())
}
private class StatusActor extends Actor {
  import StatusActor._

  var updateCount: Integer = 0
  var apiErrors: Integer = 0
  var state: State = State()

  override def receive = {
    case Read => sender ! state
    case Write => this.state = State(this.state.updateCount + 1)
    case _ =>
  }
}
