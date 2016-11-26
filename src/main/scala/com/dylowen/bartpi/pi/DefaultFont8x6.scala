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
  override val DEFAULT_WIDTH: Int = 6
  override val DEFAULT_HEIGHT: Int = 8
  override val SPACING: Int = 1

  private val START_INDEX = ' '.toInt
  private val FONT: Array[Glyph] = Array(
    stringToGlyph(
        "     " +
        "     " +
        "     " +
        "     " +
        "     " +
        "     " +
        "     " +
        "     "
    , 5), //  ' '
    UNSUPPORTED, //  '!'
    UNSUPPORTED, //  '"'
    UNSUPPORTED, //  '#'
    UNSUPPORTED, //  '$'
    UNSUPPORTED, //  '%'
    UNSUPPORTED, //  '&'
    UNSUPPORTED, //  '''
    stringToGlyph(
        "   ** " +
        "  **  " +
        " **   " +
        " **   " +
        " **   " +
        " **   " +
        "  **  " +
        "   ** "
    ), //  '('
    stringToGlyph(
        " **   " +
        "  **  " +
        "   ** " +
        "   ** " +
        "   ** " +
        "   ** " +
        "  **  " +
        " **   "
    ), //  ')'
    stringToGlyph(
        "     " +
        "* * *" +
        " *** " +
        "*****" +
        " *** " +
        "* * *" +
        "     " +
        "     "
    , 5), //  '*'
    stringToGlyph(
        "      " +
        "  **  " +
        "  **  " +
        "******" +
        "******" +
        "  **  " +
        "  **  " +
        "      "
    ), //  '+'
    stringToGlyph(
        "      " +
        "      " +
        "      " +
        "      " +
        "      " +
        "      " +
        "  *   " +
        " *    "
    ), //  ','
    stringToGlyph(
        "      " +
        "      " +
        "      " +
        " *****" +
        " *****" +
        "      " +
        "      " +
        "      "
    ), //  '-'
    stringToGlyph(
        "    " +
        "    " +
        "    " +
        "    " +
        "    " +
        "    " +
        " ** " +
        " ** "
    , 4), //  '.'
    UNSUPPORTED, //  '/'
    stringToGlyph(
        " **** " +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        " **** "
    ), //  '0'
    stringToGlyph(
        " ** " +
        "*** " +
        " ** " +
        " ** " +
        " ** " +
        " ** " +
        " ** " +
        "****"
    , 4), //  '1'
    stringToGlyph(
        " **** " +
        "**  **" +
        "    **" +
        "   ** " +
        "  **  " +
        " **   " +
        "**    " +
        "******"
    ), //  '2'
    stringToGlyph(
        " **** " +
        "**  **" +
        "    **" +
        "  *** " +
        "    **" +
        "    **" +
        "**  **" +
        " **** "
    ), //  '3'
    stringToGlyph(
        "   ** " +
        "  **  " +
        " **   " +
        "**    " +
        "** ** " +
        "******" +
        "   ** " +
        "   ** "
    ), //  '4'
    stringToGlyph(
        "******" +
        "**    " +
        "**    " +
        " **** " +
        "    **" +
        "    **" +
        "**  **" +
        " **** "
    ), //  '5'
    stringToGlyph(
        " **** " +
        "**  **" +
        "**    " +
        "**    " +
        "***** " +
        "**  **" +
        "**  **" +
        " **** "
    ), //  '6'
    stringToGlyph(
        "******" +
        "    **" +
        "    **" +
        "   ** " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  "
    ), //  '7'
    stringToGlyph(
        " **** " +
        "**  **" +
        "**  **" +
        " **** " +
        "**  **" +
        "**  **" +
        "**  **" +
        " **** "
    ), //  '8'
    stringToGlyph(
        " **** " +
        "**  **" +
        "**  **" +
        "**  **" +
        " *****" +
        "    **" +
        "**  **" +
        " **** "
    ), //  '9'
    stringToGlyph(
        "    " +
        " ** " +
        " ** " +
        "    " +
        "    " +
        " ** " +
        " ** " +
        "    "
    , 4), //  ':'
    stringToGlyph(
        "      " +
        "  **  " +
        "  **  " +
        "      " +
        "      " +
        "  **  " +
        " **   " +
        "**    "
    ), //  ';'
    UNSUPPORTED, //  '<'
    stringToGlyph(
        "      " +
        " **** " +
        " **** " +
        "      " +
        " **** " +
        " **** " +
        "      " +
        "      "
    ), //  '='
    UNSUPPORTED, //  '>'
    UNSUPPORTED, //  '?'
    UNSUPPORTED, //  '@'
    stringToGlyph(
        " **** " +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "******" +
        "**  **" +
        "**  **"
    ), //  'A'
    stringToGlyph(
        "***** " +
        "**  **" +
        "**  **" +
        "***** " +
        "**  **" +
        "**  **" +
        "**  **" +
        "***** "
    ), //  'B'
    stringToGlyph(
        " **** " +
        "**  **" +
        "**    " +
        "**    " +
        "**    " +
        "**    " +
        "**  **" +
        " **** "
    ), //  'C'
    stringToGlyph(
        "***** " +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "***** "
    ), //  'D'
    stringToGlyph(
        "******" +
        "**    " +
        "**    " +
        "***** " +
        "**    " +
        "**    " +
        "**    " +
        "******"
    ), //  'E'
    stringToGlyph(
        "******" +
        "**    " +
        "**    " +
        "***** " +
        "**    " +
        "**    " +
        "**    " +
        "**    "
    ), //  'F'
    stringToGlyph(
        " *****" +
        "**    " +
        "**    " +
        "**    " +
        "** ***" +
        "**  **" +
        "**  **" +
        " *****"
    ), //  'G'
    stringToGlyph(
        "**  **" +
        "**  **" +
        "**  **" +
        "******" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **"
    ), //  'H'
    stringToGlyph(
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  "
    ), //  'I'
    stringToGlyph(
        "   ** " +
        "   ** " +
        "   ** " +
        "   ** " +
        "   ** " +
        "   ** " +
        "** ** " +
        " ***  "

    ), //  'J'
    stringToGlyph(
        "**  **" +
        "**  **" +
        "** ** " +
        "****  " +
        "****  " +
        "** ** " +
        "**  **" +
        "**  **"
    ), //  'K'
    stringToGlyph(
        "**    " +
        "**    " +
        "**    " +
        "**    " +
        "**    " +
        "**    " +
        "******" +
        "******"
    ), //  'L'
    stringToGlyph(
        "*** ***" +
        "*******" +
        "** * **" +
        "**   **" +
        "**   **" +
        "**   **" +
        "**   **" +
        "**   **"
    , 7), //  'M'
    stringToGlyph(
        "**  **" +
        "**  **" +
        "*** **" +
        "*** **" +
        "** ***" +
        "** ***" +
        "**  **" +
        "**  **"
    ), //  'N'
    stringToGlyph(
        " **** " +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        " **** "
    ), //  'O'
    stringToGlyph(
        "***** " +
        "**  **" +
        "**  **" +
        "**  **" +
        "***** " +
        "**    " +
        "**    " +
        "**    "
    ), //  'P'
    stringToGlyph(
        " **** " +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  * " +
        " *** *"
    ), //  'Q'
    stringToGlyph(
        "***** " +
        "**  **" +
        "**  **" +
        "**  **" +
        "***** " +
        "** ** " +
        "**  **" +
        "**  **"
    ), //  'R'
    stringToGlyph(
        " **** " +
        "**  **" +
        "**    " +
        "***** " +
        " *****" +
        "    **" +
        "**  **" +
        " **** "
    ), //  'S'
    stringToGlyph(
        "******" +
        "******" +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  "
    ), //  'T'
    stringToGlyph(
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        " **** "
    ), //  'U'
    stringToGlyph(
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        " **** " +
        "  **  "
    ), //  'V'
    stringToGlyph(
        "**   *" +
        "**   *" +
        "**   *" +
        "**   *" +
        "** * *" +
        "******" +
        "*** **" +
        "**   *"
    ), //  'W'
    stringToGlyph(
        "**  **" +
        "**  **" +
        " **** " +
        "  **  " +
        "  **  " +
        " **** " +
        "**  **" +
        "**  **"
    ), //  'X'
    stringToGlyph(
        "**  **" +
        "**  **" +
        "**  **" +
        "**  **" +
        " **** " +
        "  **  " +
        "  **  " +
        "  **  "
    ), //  'Y'
    stringToGlyph(
        "******" +
        "******" +
        "    **" +
        "   ** " +
        "  **  " +
        " **   " +
        "******" +
        "******"
    ), //  'Z'
    stringToGlyph(
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
    stringToGlyph(
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
    stringToGlyph(
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
    stringToGlyph(
        "      " +
        "      " +
        "      " +
        "***** " +
        "     *" +
        " *****" +
        "**   *" +
        " *****"
    ), //  'a'
    stringToGlyph(
        "**    " +
        "**    " +
        "**    " +
        "***** " +
        "**  **" +
        "**  **" +
        "**  **" +
        "***** "
    ), //  'b'
    stringToGlyph(
        "      " +
        "      " +
        " *****" +
        "**    " +
        "**    " +
        "**    " +
        "**    " +
        " *****"
    ), //  'c'
    stringToGlyph(
        "    **" +
        "    **" +
        "    **" +
        " *****" +
        "**  **" +
        "**  **" +
        "**  **" +
        " *****"
    ), //  'd'
    UNSUPPORTED, //  'e'
    stringToGlyph(
        "   ** " +
        "  **  " +
        "  **  " +
        " **** " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  "
    ), //  'f'
    UNSUPPORTED, //  'g'
    UNSUPPORTED, //  'h'
    stringToGlyph(
        "      " +
        "  **  " +
        "      " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  " +
        "  **  "
    ), //  'i'
    stringToGlyph(
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
    stringToGlyph(
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
    stringToGlyph(
        "   ** " +
        "  **  " +
        "  **  " +
        " **   " +
        "  **  " +
        "  **  " +
        "  **  " +
        "   ** "
    ), //  '{'
    stringToGlyph(
        "  *   " +
        "  *   " +
        "  *   " +
        "  *   " +
        "  *   " +
        "  *   " +
        "  *   " +
        "  *   "
    ), //  '|'
    stringToGlyph(
        " **   " +
        "  **  " +
        "  **  " +
        "   ** " +
        "  **  " +
        "  **  " +
        "  **  " +
        " **   "
    ), //  '}'
    stringToGlyph(
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

  override def get(char: Char): Glyph = {
    if (char >= START_INDEX && char < FONT.length + START_INDEX) {
      FONT(char - START_INDEX)
    }
    else {
      UNSUPPORTED
    }
  }
}
