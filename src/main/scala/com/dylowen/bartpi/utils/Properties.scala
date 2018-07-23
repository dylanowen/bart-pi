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
  private val MainPropertyFile: String = "bart-pi.properties"
  private lazy val Properties: JProperties = {
    val inputStream: InputStream = getClass.getResourceAsStream("/" + MainPropertyFile)
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

  def get(key: String): String = Properties.getProperty(key)

  def set(key: String, value: String): Unit = Properties.setProperty(key, value)
}
