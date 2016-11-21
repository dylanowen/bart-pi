package com.dylowen.bartpi.pi

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object Gpio {

  implicit class GpioInt(val underlying: Int) extends AnyVal {
    def asMhz: Int = underlying * 1000000
  }

  implicit class BinaryStringToInt(val underlying: String) extends AnyVal {
    def b: Int = Integer.parseInt(underlying, 2)
  }

  implicit def intToByte(int: Int): Byte = int.toByte

  val LONG_BITS = java.lang.Long.SIZE
  val BYTE_BITS = java.lang.Byte.SIZE
  val BYTES_IN_LONG = LONG_BITS / BYTE_BITS

  val BYTE_MASK: Int = 0xFF

  /**
    * Java promotes bytes to integers which is super annoying for bitwise manipulation.
    * This promotes the byte, then strips out the Int garbage
    */
  def demoteByte(byte: Byte): Int = byte.toInt & BYTE_MASK
}
