package com.dylowen.bartpi.actor

import akka.actor.{ActorRef, ActorSystem, Props}

import scala.collection.concurrent.TrieMap

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
trait SingletonActor {
  private val singletonMap: TrieMap[ActorSystem, ActorRef] = new TrieMap()
  def get(implicit system: ActorSystem): ActorRef = {
    this.singletonMap.getOrElseUpdate(system, {
      system.actorOf(props, this.getClass.getName)
    })
  }
  protected def props: Props
}
