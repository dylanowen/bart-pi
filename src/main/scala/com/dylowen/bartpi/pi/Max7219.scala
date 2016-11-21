package com.dylowen.bartpi.pi

import com.dylowen.bartpi.pi.Gpio._
import com.dylowen.bartpi.utils.ApplicationLifecycle
import com.pi4j.io.spi.{SpiChannel, SpiDevice, SpiFactory}

import scala.collection.{BitSet, mutable}
import scala.util.Try

/**
  * Definitely not threadsafe
  * https://datasheets.maximintegrated.com/en/ds/MAX7219-MAX7221.pdf
  *
  * Display Coordinates
  * 0,0  →   x
  *  ↓
  *  y
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object Max7219 {
  sealed abstract class Command(val register: Byte)
  object NOOP extends Command(0x0.toByte)
  object DIGIT0 extends Command(0x1.toByte)
  object DIGIT1 extends Command(0x2.toByte)
  object DIGIT2 extends Command(0x3.toByte)
  object DIGIT3 extends Command(0x4.toByte)
  object DIGIT4 extends Command(0x5.toByte)
  object DIGIT5 extends Command(0x6.toByte)
  object DIGIT6 extends Command(0x7.toByte)
  object DIGIT7 extends Command(0x8.toByte)
  object DECODE_MODE extends Command(0x9.toByte)
  object INTENSITY extends Command(0xA.toByte)
  object SCAN_LIMIT extends Command(0xB.toByte)
  object DISPLAY_ON extends Command(0xC.toByte)
  object DISPLAY_TEST extends Command(0xF.toByte)

  implicit def CommandToByte(command: Command): Byte = command.register

  val TRUE: Byte = 0x1
  val FALSE: Byte = 0x0

  val DIGITS: Array[Command] = Array(
    DIGIT0,
    DIGIT1,
    DIGIT2,
    DIGIT3,
    DIGIT4,
    DIGIT5,
    DIGIT6,
    DIGIT7
  )

  val DISPLAY_WIDTH: Int = 8
  val DISPLAY_HEIGHT: Int = 8

  trait SpiWriter {
    def write(buffer: Array[Byte]): Unit
  }
  private val DebugWriter = new SpiWriter {
    override def write(buffer: Array[Byte]): Unit = {
      println(buffer.zipWithIndex
        .filter(_._2 % 2 == 1) // filter out the registers
        .map(_._1)
        .map((byte: Byte) => {
          // TODO definitely a bug here
          Integer.toBinaryString(byte + 0x100).substring(1).replace('0', '_').replace('1', '*')
        })
        .mkString(" ")
      )
    }
  }
}
class Max7219(val chained: Int = 1) extends ApplicationLifecycle {
  import Max7219._

  // wrap a SPI writer for local debugging
  private val spi: SpiWriter = Try(SpiFactory.getInstance(SpiChannel.CS0, 10.asMhz, SpiDevice.DEFAULT_SPI_MODE))
    .map((spiInstance) => {
      new SpiWriter {
        override def write(buffer: Array[Byte]): Unit = spiInstance.write(buffer, 0, buffer.length)
      }
    }).getOrElse(DebugWriter)
  val MAX_X = this.chained * DISPLAY_WIDTH
  val MAX_Y = DISPLAY_HEIGHT

  private val changedRows: Array[Boolean] = Array.ofDim(DISPLAY_HEIGHT)
  private val displayBuffer: Array[Byte] = Array.ofDim(this.chained * DISPLAY_HEIGHT)
  private val writeBuffer: Array[Byte] = Array.ofDim(this.chained * 2)

  // set the scan limit to work for our led matrix
  runCommand(SCAN_LIMIT, 0x7)
  // turn on the display
  runCommand(DISPLAY_ON, TRUE)
  runCommand(DISPLAY_TEST, FALSE)
  // disable decode mode
  runCommand(DECODE_MODE, 0x0)
  // default intensity
  runCommand(INTENSITY, 0x0)

  /*
  // debug display
  setBit(0, 0)
  setBit(0, MAX_Y - 1)
  setBit(MAX_X - 1, 0)
  setBit(MAX_X - 1, MAX_Y - 1)
  flush()
  Thread.sleep(100)
  */

  clear()
  flush()

  println("initialized")


  def runCommand(command: Command, data: Int): Unit = runCommand(command, data.toByte)

  def runCommand(command: Command, data: Byte): Unit = {
    for (i <- 0 until this.chained) {
      writeBuffer(i * 2) = command
      writeBuffer(i * 2 + 1) = data
    }

    write(writeBuffer)
  }

  def set(string: String, font: Font, x: Int = 0, y: Int = 0): Unit = {
    for (i <- string.indices) {
      set(string(i), font, x + i * (font.WIDTH + font.CHAR_SPACING), y)
    }
  }

  def set(char: Char, font: Font, x: Int, y: Int): Unit = set(font.get(char), font.WIDTH, font.HEIGHT, x, y)

  def set(input: BitSet, width: Int, height: Int, x: Int, y: Int): Unit = {
    for (inX <- 0 until width) {
      for (inY <- 0 until height) {
        val bit: Boolean = input(inX + inY * width)
        setBit(x + inX, y + inY, bit)
      }
    }
  }

  def setBit(x: Int, y: Int, value: Boolean = true): Unit = {
    if (x >= 0 && y >= 0 && x < MAX_X && y < MAX_Y) {
      changedRows(y) = true

      // transpose the bits to display nicely on our screen
      val bitIndex: Int = (BYTE_BITS - 1) - (x % BYTE_BITS)
      val byteIndex: Int = x / BYTE_BITS + y * this.chained
      val oldByte: Byte = displayBuffer(byteIndex)
      val newByte: Byte = if (value) {
        oldByte | (0x1 << bitIndex)
      }
      else {
        oldByte & ~(0x1 << bitIndex)
      }

      displayBuffer(byteIndex) = newByte
    }
  }

  def shiftLeft(offset: Int = 1): Unit = {
    val shiftMask = getShiftMasks(offset)

    for (y <- 0 until DISPLAY_HEIGHT) {
      changedRows(y) = true

      var carried: Byte = 0x0
      for (x <- this.chained -1 to 0 by -1) {
        val i = x + y * this.chained

        val oldByte = displayBuffer(i)
        val byte = (oldByte << offset) | carried
        displayBuffer(i) = byte

        // get the bits we care about at the top
        carried = (oldByte & shiftMask._2) >> (BYTE_BITS - offset)
        // mask out the garbage up top
        carried &= shiftMask._1
      }
    }
  }

  def shiftRight(offset: Int = 1): Unit = {
    val shiftMask = getShiftMasks(offset)

    for (y <- 0 until DISPLAY_HEIGHT) {
      changedRows(y) = true

      var carried: Byte = 0x0
      for (x <- 0 until this.chained) {
        val i = x + y * this.chained

        val oldByte = demoteByte(displayBuffer(i))
        val byte = (oldByte >> offset) | carried
        displayBuffer(i) = byte

        // get the bits we care about at the bottom
        carried = (oldByte & shiftMask._1) << (BYTE_BITS - offset)
        // mask out the garbage at the bottom
        carried &= shiftMask._2
      }
    }
  }

  private def getShiftMasks(offset: Int): (Int, Int) = {
    // byte is promoted and shifted over to give us a mask
    val shiftMaskHigh: Int = 0x80.toByte >> (offset - 1)
    val shiftMaskLow: Int = (2 ^ (offset - 1)) - 1

    (shiftMaskLow, shiftMaskHigh)
  }

  def flush(): Unit = {
    // loop over all the bytes in chunks of rows
    for (y <- 0 until DISPLAY_HEIGHT) {
      if (changedRows(y)) {
        changedRows(y) = false

        for (x <- 0 until this.chained) {
          writeBuffer(x * 2) = DIGITS(y)
          writeBuffer(x * 2 + 1) = displayBuffer(x + y * this.chained)
        }

        write(writeBuffer)
      }
    }
  }

  private def convenienceWrite(buffer: Byte*): Unit = write(buffer.toArray)
  private def write(buffer: Array[Byte]): Unit = spi.write(buffer)

  def clear(): Unit = {
    for (i <- displayBuffer.indices) {
      displayBuffer(i) = 0x0
    }
    // this is optimized but we still need it so flush works
    for (i <- changedRows.indices) {
      changedRows(i) = true
    }
  }

  def stop(): Unit = {
    clear()
    flush()
    runCommand(DISPLAY_ON, FALSE)
  }
}