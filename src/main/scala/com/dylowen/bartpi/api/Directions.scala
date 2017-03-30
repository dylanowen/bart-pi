package com.dylowen.bartpi.api

import com.dylowen.bartpi.utils.EnumBuilder

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
class Direction(val name: String, val abbr: String)
object Directions extends EnumBuilder[Direction] {
  private def register(direction: Direction): Direction = register(direction.name, direction)

  val NORTH = register(new Direction("NORTH", "n"))
  val SOUTH = register(new Direction("SOUTH", "s"))

  val UNKNOWN = new Direction("UNKNOWN", "U")

  build()
}
