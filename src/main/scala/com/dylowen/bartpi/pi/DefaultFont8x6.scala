package com.dylowen.bartpi.pi

import GpioImplicits._

import scala.collection.BitSet

/**
  * http://www.fonts2u.com/led-8x6-regular.font
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object DefaultFont8x6 extends Font {
  override val WIDTH = 6
  override val HEIGHT = 8
  override val CHAR_SPACING = 2

  private val START_INDEX = ' '.toInt
  private val FONT: Array[BitSet] = Array(
    stringToBitSet(
        "      " +
        "      " +
        "      " +
        "      " +
        "      " +
        "      " +
        "      " +
        "      "
    ), //  ' '
    UNSUPPORTED, //  '!'
    UNSUPPORTED, //  '"'
    UNSUPPORTED, //  '#'
    UNSUPPORTED, //  '$'
    UNSUPPORTED, //  '%'
    UNSUPPORTED, //  '&'
    UNSUPPORTED, //  '''
    stringToBitSet(
        "   ** " +
        "  **  " +
        " **   " +
        " **   " +
        " **   " +
        " **   " +
        "  **  " +
        "   ** "
    ), //  '('
    stringToBitSet(
        " **   " +
        "  **  " +
        "   ** " +
        "   ** " +
        "   ** " +
        "   ** " +
        "  **  " +
        " **   "
    ), //  ')'
    UNSUPPORTED, //  '*'
    UNSUPPORTED, //  '+'
    UNSUPPORTED, //  ','
    UNSUPPORTED, //  '-'
    UNSUPPORTED, //  '.'
    UNSUPPORTED, //  '/'
    UNSUPPORTED, //  ' '
    UNSUPPORTED, //  '1'
    UNSUPPORTED, //  '2'
    UNSUPPORTED, //  '3'
    UNSUPPORTED, //  '4'
    UNSUPPORTED, //  '5'
    UNSUPPORTED, //  '6'
    UNSUPPORTED, //  '7'
    UNSUPPORTED, //  '8'
    UNSUPPORTED, //  '9'
    UNSUPPORTED, //  ':'
    UNSUPPORTED, //  ';'
    UNSUPPORTED, //  '<'
    UNSUPPORTED, //  '='
    UNSUPPORTED, //  '>'
    UNSUPPORTED, //  '?'
    UNSUPPORTED, //  '@'
    stringToBitSet(
        " **** " +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "******" +
        "**  **" +
        "**  **"
    ), //  'A'
    stringToBitSet(
        "***** " +
        "**  **" +
        "**  **" +
        "***** " +
        "**  **" +
        "**  **" +
        "**  **" +
        "***** "
    ), //  'B'
    stringToBitSet(
        " **** " +
        "**  **" +
        "**    " +
        "**    " +
        "**    " +
        "**    " +
        "**  **" +
        " **** "
    ), //  'C'
    stringToBitSet(
        "***** " +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "***** "
    ), //  'D'
    stringToBitSet(
        "******" +
        "**    " +
        "**    " +
        "***** " +
        "**    " +
        "**    " +
        "**    " +
        "******"
    ), //  'E'
    stringToBitSet(
        "******" +
        "**    " +
        "**    " +
        "***** " +
        "**    " +
        "**    " +
        "**    " +
        "**    "
    ), //  'F'
    UNSUPPORTED, //  'G'
    stringToBitSet(
        "**  **" +
        "**  **" +
        "**  **" +
        "******" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **"
    ), //  'H'
    stringToBitSet(
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  "
    ), //  'I'
    UNSUPPORTED, //  'J'
    UNSUPPORTED, //  'K'
    stringToBitSet(
        "**    " +
        "**    " +
        "**    " +
        "**    " +
        "**    " +
        "**    " +
        "**    " +
        "******"
    ), //  'L'
    UNSUPPORTED, //  'M'
    UNSUPPORTED, //  'N'
    UNSUPPORTED, //  'O'
    UNSUPPORTED, //  'P'
    UNSUPPORTED, //  'Q'
    UNSUPPORTED, //  'R'
    UNSUPPORTED, //  'S'
    stringToBitSet(
        "******" +
        "******" +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  "
    ), //  'T'
    UNSUPPORTED, //  'U'
    UNSUPPORTED, //  'V'
    UNSUPPORTED, //  'W'
    UNSUPPORTED, //  'X'
    UNSUPPORTED, //  'Y'
    UNSUPPORTED, //  'Z'
    UNSUPPORTED, //  '['
    UNSUPPORTED, //  backslash
    UNSUPPORTED, //  ']'
    UNSUPPORTED, //  '^'
    UNSUPPORTED, //  '_'
    UNSUPPORTED, //  '`'
    UNSUPPORTED, //  'a'
    UNSUPPORTED, //  'b'
    UNSUPPORTED, //  'c'
    UNSUPPORTED, //  'd'
    UNSUPPORTED, //  'e'
    UNSUPPORTED, //  'f'
    UNSUPPORTED, //  'g'
    UNSUPPORTED, //  'h'
    UNSUPPORTED, //  'i'
    UNSUPPORTED, //  'j'
    UNSUPPORTED, //  'k'
    UNSUPPORTED, //  'l'
    UNSUPPORTED, //  'm'
    UNSUPPORTED, //  'n'
    UNSUPPORTED, //  'o'
    UNSUPPORTED, //  'p'
    UNSUPPORTED, //  'q'
    UNSUPPORTED, //  'r'
    UNSUPPORTED, //  's'
    UNSUPPORTED, //  't'
    UNSUPPORTED, //  'u'
    UNSUPPORTED, //  'v'
    UNSUPPORTED, //  'w'
    UNSUPPORTED, //  'x'
    UNSUPPORTED, //  'y'
    UNSUPPORTED, //  'z'
    stringToBitSet(
        "   ** " +
        "  **  " +
        "  **  " +
        " ***  " +
        " ***  " +
        "  **  " +
        "  **  " +
        "   ** "
    ), //  '{'
    UNSUPPORTED, //  '|'
    stringToBitSet(
        " **   " +
        "  **  " +
        "  **  " +
        "  *** " +
        "  *** " +
        "  **  " +
        "  **  " +
        " **   "
    ), //  '}'
    UNSUPPORTED //  '~'
  )

  override def get(char: Char): BitSet = {
    if (char >= START_INDEX && char <= FONT.length) {
      FONT(char - START_INDEX)
    }
    else {
      UNSUPPORTED
    }
  }
}
