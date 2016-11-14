package com.dylowen.bartpi.actor

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object UpdateBartActor {
  def props: Props = Props(new UpdateBartActor())
}
class UpdateBartActor extends Actor {
  private implicit val system: ActorSystem = context.system
  private val bartActor: ActorRef = StatusActor.get

  override def receive = {
    case _ => println("Scheduled")
      this.bartActor ! StatusActor.Write
  }
}
