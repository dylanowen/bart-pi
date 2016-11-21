package com.dylowen.bartpi.actor

import akka.actor.{Actor, ActorLogging, Props}

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object ScreenActor extends SingletonActor {


  override def props = Props(new ScreenActor())
}
class ScreenActor extends Actor with ActorLogging {
  override def receive = {
    null
  }
}
