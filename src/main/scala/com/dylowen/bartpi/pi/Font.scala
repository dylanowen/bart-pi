package com.dylowen.bartpi.pi

import scala.collection.BitSet

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
trait Font {
  val DEFAULT_WIDTH: Int
  val DEFAULT_HEIGHT: Int
  val SPACING: Int

  def get(char: Char): Glyph
  def get(string: String): Array[Glyph] = string.map(get).toArray

  // use filled space for unsupported
  protected lazy val UNSUPPORTED: Glyph = stringToGlyph("*" * DEFAULT_WIDTH * DEFAULT_HEIGHT)

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
