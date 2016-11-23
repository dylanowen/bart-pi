package com.dylowen.bartpi.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.dylowen.bartpi.actor.StatusActor.{State, Write}
import com.dylowen.bartpi.pi.{DefaultFont8x6, Font, Max7219}

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object ScrollingDisplayActor extends SingletonActor {
  case class RegisterMax(max7219: Max7219)
  case class DisplayMessage(message: String, repeat: Boolean = false)
  case object Tick

  override def props = Props(new ScrollingDisplayActor())
}
class ScrollingDisplayActor() extends Actor with ActorLogging {
  import ScrollingDisplayActor._

  private val font: Font = DefaultFont8x6
  private var max: Option[Max7219] = None
  private var message: String = ""
  private var charIndex: Int = 0
  private var charScrollIndex: Int = 0

  override def receive = {
    case RegisterMax(newMax) => this.max = Some(newMax)
    case DisplayMessage(newMessage, repeat) => {
      log.debug("Show Message: " + message)

      this.message = newMessage + " "
      this.charIndex = 0
      this.charScrollIndex = 0
    }
    case Tick => {
      this.max.foreach(realMax => {
        if (message.length > 0) {
          val char = message(charIndex)
          realMax.shiftLeft()
          realMax.set(char, font, realMax.MAX_X - 1 - charScrollIndex, 0)
          realMax.flush()

          charScrollIndex += 1

          if (charScrollIndex >= font.WIDTH + font.CHAR_SPACING) {
            charScrollIndex = 0
            charIndex += 1
            if (charIndex >= message.length) {
              charIndex = 0
            }
          }
        }
      })
    }
    case error => log.error("Invalid Request: " + error)
  }
}
