package com.dylowen.bartpi.utils

import java.io.InputStream
import java.util.{Properties => JProperties}

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object Properties {
  private val MAIN_PROPERTY_FILE = "bart-pi.properties"
  private lazy val PROPERTIES: JProperties = {
    val inputStream: InputStream = Properties.getClass.getResourceAsStream("/" + MAIN_PROPERTY_FILE)
    try {
      val properties: JProperties = new JProperties()
      properties.load(inputStream)

      properties
    }
    finally {
      if (inputStream != null) {
        inputStream.close()
      }
    }
  }

  def get(key: String) = PROPERTIES.getProperty(key)
  def set(key: String, value: String) = PROPERTIES.setProperty(key, value)
}
