package com.dylowen.bartpi.pi

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object GpioImplicits {

  implicit class GpioInt(val underlying: Int) extends AnyVal {
    def asMhz: Int = underlying * 1000000
  }

  implicit class BinaryStringToInt(val underlying: String) extends AnyVal {
    def b: Int = Integer.parseInt(underlying, 2)
  }

  implicit def intToByte(int: Int): Byte = int.toByte
}
