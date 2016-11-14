package com.dylowen.bartpi

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object UpdateBartActor {
  def props(bartActor: ActorRef)(implicit system: ActorSystem): Props = Props(new UpdateBartActor(bartActor, system))
}
class UpdateBartActor(private val bartActor: ActorRef, private val system: ActorSystem) extends Actor {

  override def receive = {
    case _ => println("lkjsdf")
      this.bartActor ! BartActor.Write
  }
}
