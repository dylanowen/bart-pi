package com.dylowen.bartpi.pi

import com.dylowen.bartpi.pi.Gpio._
import com.pi4j.io.spi.SpiChannel

import scala.collection.BitSet

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
}

class Max7219(channel: SpiChannel, val chained: Int) {
  import Max7219._

  val MAX_X: Int = this.chained * DISPLAY_WIDTH
  val MAX_Y: Int = DISPLAY_HEIGHT

  private val spi: Max7219Device = Max7219Device.get(channel, chained)

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

  clear()
  flush()

  //set(DefaultFont8x6.RAINY, 0, 0)
  //flush()
  //Thread.sleep(100000)

  def runCommand(command: Command, data: Int): Unit = runCommand(command, data.toByte)

  def runCommand(command: Command, data: Byte): Unit = {
    for (i <- 0 until this.chained) {
      writeBuffer(i * 2) = command
      writeBuffer(i * 2 + 1) = data
    }

    write(writeBuffer)
  }

  def set(string: String, font: Font, x: Int = 0, y: Int = 0): Unit = {
    var offset: Int = x
    for (i <- string.indices) {
      val glyph: Glyph = font.get(string(i))
      set(glyph, offset, y)

      offset += glyph.width + font.SPACING
    }
  }

  def set(char: Glyph, x: Int, y: Int): Unit = set(char, 0, x, y)

  def set(char: Glyph, frame: Int, x: Int, y: Int): Unit = set(char.getFrame(frame), char.width, char.height, x, y)

  def set(input: BitSet, width: Int, height: Int, x: Int, y: Int): Unit = {
    for (inX <- 0 until width) {
      for (inY <- 0 until height) {
        val bit: Boolean = input(inX + inY * width)
        setBit(x + inX, y + inY, bit)
      }
    }
  }

  def shiftLeft(): Unit = {
    val shiftMask = getShiftMasks(1)

    for (y <- 0 until DISPLAY_HEIGHT) {
      changedRows(y) = true

      var carried: Byte = 0x0
      for (x <- this.chained -1 to 0 by -1) {
        val i = x + y * this.chained

        val oldByte = displayBuffer(i)
        val byte = (oldByte << 1) | carried
        displayBuffer(i) = byte

        // get the bits we care about at the top
        carried = (oldByte & shiftMask._2) >> (ByteBits - 1)
        // mask out the garbage up top
        carried &= shiftMask._1
      }
    }
  }

  def shiftRight(): Unit = {
    val shiftMask = getShiftMasks(1)

    for (y <- 0 until DISPLAY_HEIGHT) {
      changedRows(y) = true

      var carried: Byte = 0x0
      for (x <- 0 until this.chained) {
        val i = x + y * this.chained

        val oldByte = demoteByte(displayBuffer(i))
        val byte = (oldByte >> 1) | carried
        displayBuffer(i) = byte

        // get the bits we care about at the bottom
        carried = (oldByte & shiftMask._1) << (ByteBits - 1)
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

  def shiftDown(): Unit = shiftY(0 until DISPLAY_HEIGHT)

  def shiftUp(): Unit = shiftY(DISPLAY_HEIGHT - 1 to 0 by -1)

  private def shiftY(indices: Range): Unit = {
    val carried: Array[Byte] = Array.ofDim(this.chained)
    for (y <- indices) {
      changedRows(y) = true

      for (x <- 0 until this.chained) {
        val i: Int = x + y * this.chained
        val temp: Byte = displayBuffer(i)

        displayBuffer(i) = carried(x)
        carried(x) = temp
      }
    }
  }

  def setBit(x: Int, y: Int, value: Boolean = true): Unit = {
    if (x >= 0 && y >= 0 && x < MAX_X && y < MAX_Y) {
      // mark this row as having changed
      changedRows(y) = true

      // transpose the bits to display nicely on our screen
      val bitIndex: Int = (ByteBits - 1) - (x % ByteBits)
      // get the index of our real byte
      val byteIndex: Int = x / ByteBits + y * this.chained
      val oldByte: Byte = displayBuffer(byteIndex)
      // OR or AND depending on if we're setting or clearing
      val newByte: Byte = if (value) {
        oldByte | (0x1 << bitIndex)
      }
      else {
        oldByte & ~(0x1 << bitIndex)
      }

      displayBuffer(byteIndex) = newByte
    }
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