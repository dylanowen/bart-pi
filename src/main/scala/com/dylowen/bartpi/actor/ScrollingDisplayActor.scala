package com.dylowen.bartpi.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.dylowen.bartpi.pi.{DefaultFont8x6, Font, Glyph, Max7219}

import scala.collection.immutable.ListSet
import scala.collection.mutable

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

  // Our messages are based around actors. This might not be the best but it works for now
  class InternalMessage(val ref: ActorRef, message: String, repeat: Boolean) extends DisplayMessage(message, repeat) {
    // our set is based around the reference so use that as our hash
    override def hashCode(): Int = ref.hashCode()
    override def equals(obj: scala.Any): Boolean = ref.equals(obj)
  }

  override def props = Props(new ScrollingDisplayActor())
}
class ScrollingDisplayActor() extends Actor with ActorLogging {
  import ScrollingDisplayActor._

  private val font: Font = DefaultFont8x6
  private var maybeMax: Option[Max7219] = None

  private var messageQueue: mutable.ListBuffer[InternalMessage] = new mutable.ListBuffer[InternalMessage]()
  messageQueue += new InternalMessage(null, "*BOOT UP*", false)
  //messageQueue += new InternalMessage(null, (' ' to '~').toArray.mkString(""), false)

  private var currentMessage: String = ""
  private var charIndex: Int = 0
  private var charScrollIndex: Int = 0

  override def receive: Actor.Receive = {
    case RegisterMax(newMax) => this.maybeMax = Some(newMax)
    case DisplayMessage(message, repeat) => {
      log.debug("Show Message: " + message)

      val internalMessage: InternalMessage = new InternalMessage(sender(), message, repeat)

      // see if we have an existing message from this sender
      val found = this.messageQueue.zipWithIndex
          .find({case (internal, _) =>
            // get our message that matches the incoming message
            internal.ref == internalMessage.ref
          })
          .exists({ case (_, index) =>
            if (internalMessage.message.isEmpty) {
              // if our message is empty delete the message
              this.messageQueue.remove(index)
            }
            else {
              // update our message queue with the new message
              this.messageQueue.update(index, internalMessage)
            }

            true
          })

      // we didn't find anything add it to the end
      if (!found && !internalMessage.message.isEmpty) {
        this.messageQueue += internalMessage
      }
    }
    case Tick => this.maybeMax.foreach(max => {
      max.shiftLeft()

      var displayMessage = true
      if (this.charIndex >= this.currentMessage.length) {
        displayMessage = initNextMessage()
      }

      if (displayMessage) {
        val glyph: Glyph = font.get(this.currentMessage(charIndex))

        max.set(glyph, max.MAX_X - 1 - charScrollIndex, 0)

        charScrollIndex += 1

        if (charScrollIndex >= glyph.width + font.SPACING) {
          charScrollIndex = 0
          charIndex += 1
        }
      }

      max.flush()
    })
    case error => log.error("Invalid Request: " + error)
  }

  private def initNextMessage(): Boolean = {
    if (this.messageQueue.nonEmpty) {
      val message: InternalMessage = this.messageQueue.head
      this.messageQueue = this.messageQueue.tail
      if (message.repeat) {
        // if this is repeating, add it to the end
        this.messageQueue += message
      }

      this.currentMessage = message.message + "  "
      this.charIndex = 0
      this.charScrollIndex = 0

      true
    }
    else  {
      false
    }
  }
}
