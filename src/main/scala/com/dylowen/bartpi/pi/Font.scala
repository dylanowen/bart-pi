package com.dylowen.bartpi.pi

import scala.collection.BitSet

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
trait Font {
  val WIDTH: Int
  val HEIGHT: Int
  val CHAR_SPACING: Int

  def get(char: Char): BitSet
  def get(string: String): Array[BitSet] = string.map(get).toArray

  // use filled space for unsupported
  protected lazy val UNSUPPORTED: BitSet = stringToBitSet("*" * WIDTH * HEIGHT)

  protected def stringToBitSet(rawString: String): BitSet = {
    assert(rawString.length == WIDTH * HEIGHT)
    val builder = BitSet.newBuilder
    builder.sizeHint(rawString.length)

    for (i <- rawString.indices) {
      if (rawString(i) == '*') {
        builder += i
      }
    }

    builder.result()
  }
}
