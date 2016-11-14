package com.dylowen.bartpi

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
  lazy val Main: JProperties = {
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
}
