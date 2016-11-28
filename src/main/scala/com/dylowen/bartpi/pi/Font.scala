package com.dylowen.bartpi.pi

import scala.collection.BitSet

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object Font {
  // Symbols
  val DEGREE: Char = '\u00B0'

  // Emoji
  val SUNNY: Char = '\u2600'
  val CLOUDY: Char = '\u2601'
  val PARTLY_SUNNY: Char = '\u2601'
}
trait Font {
  val DEFAULT_WIDTH: Int
  val DEFAULT_HEIGHT: Int
  val SPACING: Int

  def get(char: Char): Glyph
  def get(string: String): Array[Glyph] = string.map(get).toArray

  // use filled space for unsupported
  protected lazy val UNSUPPORTED: Glyph = {
    val width: Int = if (DEFAULT_WIDTH % 2 == 1) DEFAULT_WIDTH else DEFAULT_WIDTH - 1
    stringToGlyph("* " * (width * DEFAULT_HEIGHT / 2), width)
  }

  protected def stringToGlyph(rawString: String, width: Int = DEFAULT_WIDTH, height: Int = DEFAULT_HEIGHT): Glyph = {
    assert(rawString.length == width * height)
    val builder = BitSet.newBuilder
    builder.sizeHint(rawString.length)

    for (i <- rawString.indices) {
      if (rawString(i) == '*') {
        builder += i
      }
    }

    Glyph(builder.result(), width, height)
  }
}
case class Glyph(bits: BitSet, width: Int, height: Int)
