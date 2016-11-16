package com.dylowen.bartpi.utils

import scala.collection.immutable.HashMap
import scala.collection.mutable

/**
  * No promises of synchronization
  *
  * @author dylan.owen
  * @since Nov-2016
  */
abstract class EnumBuilder[A] {
  private val builder: mutable.MapBuilder[String, A, Map[String, A]] = new mutable.MapBuilder[String, A, Map[String, A]](new HashMap[String, A])
  private var map: Map[String, A] = _

  private def normalizeKey(key: String): String = key.toLowerCase

  protected def register(key: String, value: A): A = {
    builder += (normalizeKey(key) -> value)
    value
  }

  protected def build(): Unit = {
    this.map = builder.result()
  }

  implicit def getByString(key: String): Option[A] = this.map.get(normalizeKey(key))
}
