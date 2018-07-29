package com.dylowen.bartpi.utils

import java.time.Duration
import java.util.concurrent.TimeUnit

import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.JavaConverters._
import scala.collection.immutable
import scala.concurrent.duration.FiniteDuration
import scala.reflect.ClassTag

/**
  * TODO add description
  *
  * @author dylan.owen
  * @since Jul-2018
  */
object Properties {
  private lazy val properties: Properties = {
    // configuration from the command line
    val configOverrides: Config = ConfigFactory.defaultOverrides()

    // standard settings
    val standardConfig: Config = ConfigFactory.load()

    val config: Config = configOverrides
      .withFallback(standardConfig)
      .resolve()

    new Properties(config)
  }

  def apply(): Properties = properties
}

class Properties(config: Config) {

  private val str: Class[String] = classOf[String]
  private val strSeq: Class[immutable.Seq[String]] = classOf[immutable.Seq[String]]
  private val int: Class[Int] = classOf[Int]
  private val finiteDuration: Class[FiniteDuration] = classOf[FiniteDuration]

  def get[T: PropertyTypesContainer](key: String)(implicit evidence: ClassTag[T]): Option[T] = {
    if (config.hasPath(key)) {
      Some((evidence.runtimeClass match {
        case `str` => config.getString(key)
        case `strSeq` => config.getStringList(key).asScala.to[immutable.Seq]
        case `int` => config.getInt(key)
        case `finiteDuration` => {
          val duration: Duration = config.getDuration(key)

          // this probably won't overflow since 2^63 - 1 ns is ~= 292 years
          FiniteDuration(duration.toNanos, TimeUnit.NANOSECONDS).toCoarsest
        }
      }).asInstanceOf[T])
    }
    else {
      None
    }
  }

  def getUnsafe[T: PropertyTypesContainer](key: String)(implicit evidence: ClassTag[T]): T = get(key).get
}

object PropertyTypesContainer {

  implicit object StringWitness extends PropertyTypesContainer[String]

  implicit object StringSeqWitness extends PropertyTypesContainer[immutable.Seq[String]]

  implicit object IntWitness extends PropertyTypesContainer[Int]

  implicit object FiniteDurationWitness extends PropertyTypesContainer[FiniteDuration]

}

sealed class PropertyTypesContainer[T]
