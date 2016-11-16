package com.dylowen.bartpi.api

import com.dylowen.bartpi.utils.EnumBuilder

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
class Direction(val name: String)
object Directions extends EnumBuilder[Direction] {
  private def register(direction: Direction): Direction = register(direction.name, direction)

  val NORTH = register(new Direction("NORTH"))
  val EAST = register(new Direction("EAST"))
  val SOUTH = register(new Direction("SOUTH"))
  val WEST = register(new Direction("WEST"))

  build()
}
