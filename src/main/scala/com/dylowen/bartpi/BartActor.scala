package com.dylowen.bartpi

import akka.actor.{Actor, Props}
import akka.actor.Actor.Receive

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object BartActor {
  case object Read
  case class Write()

  final case class State(updateCount: Integer = 0)

  def props: Props = Props(new BartActor())
}

class BartActor extends Actor {
  import BartActor._

  var state: State = State()

  override def receive = {
    case Read => sender ! this.state
    case Write => this.state = State(this.state.updateCount + 1)
    case _ =>
  }
}
