package com.dylowen.bartpi.api

import com.dylowen.bartpi.utils.EnumBuilder
import com.sun.corba.se.spi.orbutil.fsm.Guard.Result

import scala.util.parsing.combinator.{Parsers, RegexParsers}

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
sealed trait LineToken
class Line(val color: String) extends LineToken
object Lines extends EnumBuilder[Line] with RegexParsers {
  private def register(line: Line): Line = register(line.color, line)

  val RED = register(new Line("RED"))
  val ORANGE = register(new Line("ORANGE"))
  val YELLOW = register(new Line("YELLOW"))
  val GREEN = register(new Line("GREEN"))
  val BLUE = register(new Line("BLUE"))

  build()

  case class LineLexerError(message: String)
  case object Comma extends LineToken

  override def skipWhitespace = true
  override val whiteSpace = "[ \t\r\f]+".r

  private def lineParser: Parser[Line] = "(?i)(red|orange|yellow|green|blue)".r ^^ { line => getByString(line).get }
  private def comma = "," ^^ (_ => Comma)

  private def tokens: Parser[List[Line]] = {
    phrase(rep1(lineParser, lineParser | comma)) ^^ {
      tokens => tokens.flatMap({ //filter out the tokens we don't care about
        case line: Line => Some(line)
        case _ => None
      })
    }
  }

  def parseLines(code: String): Either[LineLexerError, Set[Line]] = {
    parse(tokens, code) match {
      case Success(result, next) => Right(result.toSet)
      case NoSuccess(message, next) => Left(LineLexerError(message))
    }
  }
}
