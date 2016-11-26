package com.dylowen.bartpi.pi

import java.awt.{Color, Graphics}
import javax.swing.{JFrame, JPanel}

import com.dylowen.bartpi.pi.Gpio._
import com.pi4j.io.spi.{SpiChannel, SpiDevice, SpiFactory}

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object Max7219Device {
  def get(channel: SpiChannel, chained: Int = 1): Max7219Device = {
    // try to get the real device, and fall back to a fake one for debugging
    try {
      val device: SpiDevice = SpiFactory.getInstance(channel, 10.asMhz, SpiDevice.DEFAULT_SPI_MODE)

      new Max7219Device {
        override def write(buffer: Array[Byte]): Unit = device.write(buffer, 0, buffer.length)
      }
    }
    catch {
      // catch every exception for local debugging
      case _ => new Max7219Simulator(chained)
    }
  }
}
trait Max7219Device {
  def write(buffer: Array[Byte]): Unit
}

object Max7219Simulator {
  val CELL_SIZE: Int = 25
  val LED_SIZE: Int = 20
}
class Max7219Simulator(private val chained: Int = 1) extends Max7219Device {
  val HEIGHT: Int = Max7219.DISPLAY_HEIGHT * Max7219Simulator.CELL_SIZE
  val WIDTH: Int = Max7219.DISPLAY_WIDTH * Max7219Simulator.CELL_SIZE * this.chained

  private val window: JFrame = new JFrame()
  window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

  private val drawingPanel: DrawingPanel = new DrawingPanel()
  window.add(drawingPanel)

  // pad the output so it'll all showup on the screen
  window.setSize(WIDTH, HEIGHT + Max7219Simulator.CELL_SIZE)
  window.setVisible(true)

  override def write(buffer: Array[Byte]): Unit = {
    val rowIndex: Int = getDigitRow(buffer(0))

    if (rowIndex != -1 && buffer.length == this.chained * 2) {
      renderRow(rowIndex, buffer.zipWithIndex.filter(_._2 % 2 == 1).map(_._1))
    }
    else {
      // we're not doing a graphical operation so just print the hex
      println(buffer
        .map((byte: Byte) => {
          "0x" + Integer.toHexString(demoteByte(byte) + 0x100).substring(1)
        })
        .mkString(" ")
      )
    }
  }

  private def renderRow(rowIndex: Int, buffer: Array[Byte]): Unit = {
    for (col <- buffer.indices) {
      for (i <- 0 to 7) {
        val x = (col * Max7219.DISPLAY_WIDTH) + i
        val y = rowIndex
        val on: Boolean = (buffer(col) >> (Gpio.BYTE_BITS - 1 - i) & 0x1) == 1

        this.drawingPanel.drawMatrix(x)(y) = on
      }
    }

    this.drawingPanel.repaint()
  }

  private def getDigitRow(byte: Byte): Int = Max7219.DIGITS.indexOf(Max7219.DIGITS.find(_.register == byte).getOrElse(Max7219.NOOP))

  private class DrawingPanel() extends JPanel {
    val drawMatrix: Array[Array[Boolean]] = Array.ofDim(Max7219.DISPLAY_WIDTH * chained, Max7219.DISPLAY_HEIGHT)

    override def paintComponent(g: Graphics): Unit = {
      g.setColor(Color.BLACK)
      g.fillRect(0, 0, getWidth, getHeight)

      for (i <- this.drawMatrix.indices) {
        for (j <- this.drawMatrix(i).indices) {
          val on: Boolean = this.drawMatrix(i)(j)
          val x = i * Max7219Simulator.CELL_SIZE
          val y = j * Max7219Simulator.CELL_SIZE

          if (on) {
            g.setColor(Color.RED)
            g.fillOval(x, y, Max7219Simulator.LED_SIZE, Max7219Simulator.LED_SIZE)
          }
        }
      }
    }
  }
}
