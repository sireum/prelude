/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory

object LoggingUtil {
  object Level extends Enumeration {
    type Type = Value
    val All, Info, Error, Debug, Trace, Off = Value
  }

  sealed trait Mode
  object None extends Mode
  object Console extends Mode
  final case class File(uri : FileResourceUri) extends Mode

  def logger(o : AnyRef) = Logger(LoggerFactory getLogger o.getClass.getName)

  def setup(o : AnyRef, level : Level.Type = Level.All, mode : Mode) {
    import java.util.logging.{
      LogManager,
      Handler,
      ConsoleHandler,
      FileHandler,
      Level => JLevel
    }

    val l = logger(o)
    val name = l.underlying.getName
    val jl = LogManager.getLogManager.getLogger(name)
    val jlevel = level match {
      case Level.All   => JLevel.ALL
      case Level.Info  => JLevel.INFO
      case Level.Error => JLevel.SEVERE
      case Level.Debug => JLevel.FINE
      case Level.Trace => JLevel.FINEST
      case Level.Off   => JLevel.OFF
    }
    jl.setLevel(jlevel)
    import scala.collection.JavaConversions._
    mode match {
      case None =>
        for (h <- jl.getHandlers)
          jl.removeHandler(h)
      case Console =>
        val h = new ConsoleHandler
        h.setLevel(jlevel)
        jl.addHandler(h)
      case File(uri) =>
        val h = new FileHandler(
          new java.io.File(new java.net.URI(uri)).getAbsolutePath)
        h.setLevel(jlevel)
        jl.addHandler(h)
    }
  }
}

trait ImplicitLogging {
  implicit lazy val logger = Logger(LoggerFactory getLogger getClass.getName)
}