package com.dylowen.bartpi.pi

import Gpio._

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
  override val CHAR_SPACING = 1

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
    stringToBitSet(
        "      " +
        "      " +
        "      " +
        "      " +
        "      " +
        "      " +
        "  *   " +
        " *    "
    ), //  ','
    stringToBitSet(
        "      " +
        "      " +
        "      " +
        " *****" +
        " *****" +
        "      " +
        "      " +
        "      "
    ), //  '-'
    stringToBitSet(
        "      " +
        "      " +
        "      " +
        "      " +
        "      " +
        "      " +
        "      " +
        " *    "
    ), //  '.'
    UNSUPPORTED, //  '/'
    stringToBitSet(
        " **** " +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        " **** "
    ), //  '0'
    stringToBitSet(
        "  **  " +
        " ***  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        " **** "
    ), //  '1'
    stringToBitSet(
        " **** " +
        "**  **" +
        "    **" +
        "   ** " +
        "  **  " +
        " **   " +
        "**    " +
        "******"
    ), //  '2'
    stringToBitSet(
        " **** " +
        "**  **" +
        "    **" +
        "  *** " +
        "    **" +
        "    **" +
        "**  **" +
        " **** "
    ), //  '3'
    UNSUPPORTED, //  '4'
    UNSUPPORTED, //  '5'
    UNSUPPORTED, //  '6'
    UNSUPPORTED, //  '7'
    stringToBitSet(
        " **** " +
        "**  **" +
        "**  **" +
        " **** " +
        "**  **" +
        "**  **" +
        "**  **" +
        " **** "
    ), //  '8'
    stringToBitSet(
        " **** " +
        "**  **" +
        "**  **" +
        "**  **" +
        " *****" +
        "    **" +
        "**  **" +
        " **** "
    ), //  '9'
    UNSUPPORTED, //  ':'
    UNSUPPORTED, //  ';'
    UNSUPPORTED, //  '<'
    stringToBitSet(
        "      " +
        "      " +
        " **** " +
        "      " +
        " **** " +
        "      " +
        "      " +
        "      "
    ), //  '='
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
    stringToBitSet(
        " *****" +
        "**    " +
        "**    " +
        "**    " +
        "** ***" +
        "**  **" +
        "**  **" +
        " *****"
    ), //  'G'
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
    stringToBitSet(
        "   ** " +
        "   ** " +
        "   ** " +
        "   ** " +
        "   ** " +
        "   ** " +
        "** ** " +
        " ***  "

    ), //  'J'
    stringToBitSet(
        "**  **" +
        "**  **" +
        "** ** " +
        "****  " +
        "****  " +
        "** ** " +
        "**  **" +
        "**  **"
    ), //  'K'
    stringToBitSet(
        "**    " +
        "**    " +
        "**    " +
        "**    " +
        "**    " +
        "**    " +
        "******" +
        "******"
    ), //  'L'
    stringToBitSet(
        "***  *" +
        "******" +
        "** * *" +
        "**   *" +
        "**   *" +
        "**   *" +
        "**   *" +
        "**   *"
    ), //  'M'
    stringToBitSet(
        "**  **" +
        "**  **" +
        "*** **" +
        "*** **" +
        "** ***" +
        "** ***" +
        "**  **" +
        "**  **"
    ), //  'N'
    stringToBitSet(
        " **** " +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        " **** "
    ), //  'O'
    stringToBitSet(
        "***** " +
        "**  **" +
        "**  **" +
        "**  **" +
        "***** " +
        "**    " +
        "**    " +
        "**    "
    ), //  'P'
    stringToBitSet(
        " **** " +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  * " +
        " *** *"
    ), //  'Q'
    stringToBitSet(
        "***** " +
        "**  **" +
        "**  **" +
        "**  **" +
        "***** " +
        "** ** " +
        "**  **" +
        "**  **"
    ), //  'R'
    stringToBitSet(
        " **** " +
        "**  **" +
        "**    " +
        "***** " +
        " *****" +
        "    **" +
        "**  **" +
        " **** "
    ), //  'S'
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
    stringToBitSet(
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        " **** "
    ), //  'U'
    stringToBitSet(
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        " **** " +
        "  **  "
    ), //  'V'
    stringToBitSet(
        "**   *" +
        "**   *" +
        "**   *" +
        "**   *" +
        "** * *" +
        "******" +
        "*** **" +
        "**   *"
    ), //  'W'
    stringToBitSet(
        "**  **" +
        "**  **" +
        " **** " +
        "  **  " +
        "  **  " +
        " **** " +
        "**  **" +
        "**  **"
    ), //  'X'
    stringToBitSet(
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        " **** " +
        "  **  " +
        "  **  " +
        "  **  "
    ), //  'Y'
    stringToBitSet(
        "******" +
        "******" +
        "    **" +
        "   ** " +
        "  **  " +
        " **   " +
        "******" +
        "******"
    ), //  'Z'
    stringToBitSet(
        "***   " +
        "*     " +
        "*     " +
        "*     " +
        "*     " +
        "*     " +
        "*     " +
        "***   "
    ), //  '['
    UNSUPPORTED, //  '/'
    stringToBitSet(
        "   ***" +
        "     *" +
        "     *" +
        "     *" +
        "     *" +
        "     *" +
        "     *" +
        "   ***"
    ), //  ']'
    UNSUPPORTED, //  '^'
    stringToBitSet(
        "      " +
        "      " +
        "      " +
        "      " +
        "      " +
        "      " +
        "      " +
        "******"
    ), //  '_'
    UNSUPPORTED, //  '`'
    UNSUPPORTED, //  'a'
    UNSUPPORTED, //  'b'
    UNSUPPORTED, //  'c'
    UNSUPPORTED, //  'd'
    UNSUPPORTED, //  'e'
    UNSUPPORTED, //  'f'
    UNSUPPORTED, //  'g'
    UNSUPPORTED, //  'h'
    stringToBitSet(
        "      " +
        "  **  " +
        "      " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  "
    ), //  'i'
    stringToBitSet(
        "      " +
        "   ** " +
        "      " +
        "   ** " +
        "   ** " +
        "   ** " +
        "   ** " +
        "  **  "
    ), //  'j'
    UNSUPPORTED, //  'k'
    stringToBitSet(
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  "
    ), //  'l'
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
        "  **  " +
        "  **  " +
        "  **  " +
        "   ** "
    ), //  '{'
    stringToBitSet(
        "  *   " +
        "  *   " +
        "  *   " +
        "  *   " +
        "  *   " +
        "  *   " +
        "  *   " +
        "  *   "
    ), //  '|'
    stringToBitSet(
        " **   " +
        "  **  " +
        "  **  " +
        "  *** " +
        "  **  " +
        "  **  " +
        "  **  " +
        " **   "
    ), //  '}'
    stringToBitSet(
        "      " +
        "      " +
        "      " +
        " ** **" +
        "** ** " +
        "      " +
        "      " +
        "      "
    ) //  '~'
  )

  /*
  val ALIEN: BitSet = stringToBitSet(
      " ***** " +
      "*     *" +
      "* * * *" +
      "* * * *" +
      "*     *" +
      " * ** *" +
      "  *  * " +
      "   **  "
  )
  */

  override def get(char: Char): BitSet = {
    if (char >= START_INDEX && char < FONT.length + START_INDEX) {
      FONT(char - START_INDEX)
    }
    else {
      UNSUPPORTED
    }
  }
}
