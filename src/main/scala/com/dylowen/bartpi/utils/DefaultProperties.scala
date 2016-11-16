package com.dylowen.bartpi.utils

import java.io.InputStream
import java.util.Properties

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Nov-2016
  */
object DefaultProperties {
  private val MAIN_PROPERTY_FILE = "bart-pi.properties"
  lazy val Main: Properties = {
    val inputStream: InputStream = DefaultProperties.getClass.getResourceAsStream("/" + MAIN_PROPERTY_FILE)
    try {
      val properties: Properties = new Properties()
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
