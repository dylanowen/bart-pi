package com.dylowen.bartpi.pi

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
class ScrollingDisplay(private val max: Max7219) {
  private var string: String = (for (c <- ' ' to '~') yield c.toChar).mkString
  private var tickInterval: Int = 50
  private val font: Font = DefaultFont8x6

  def run(): Unit = {
    println(string)
    for (char <- string) {
      for (i <- 0 until font.WIDTH + font.CHAR_SPACING) {
        max.shiftLeft()
        max.set(char, font, max.MAX_X - 1 - i, 0)

        max.flush()

        Thread.sleep(tickInterval)
      }
    }
  }
}
