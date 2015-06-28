/*
Copyright (c) 2014 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

import java.io.{ File => JFile }
import java.io.FileReader
import java.io.FileWriter
import java.util._

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object Config {
  final val SIREUM_PROPERTIES = "sireum.properties"
  private var cache : IMap[String, String] = _
  private var lastModified : Long = _

  def configDir : JFile = {
    OsArchUtil.detect match {
      case OsArch.Win32 | OsArch.Win64 =>
        new JFile(System.getenv("APPDATA") + "/Sireum")
      case _ =>
        new JFile(System.getProperty("user.home") + "/.sireum")
    }
  }

  def load : IMap[String, String] = synchronized {
    import scala.collection.JavaConversions._
    var result = imapEmpty[String, String]
    propFile match {
      case Some(f) =>
        val fLastModified = f.lastModified
        if (fLastModified != lastModified) {
          val p = new Properties
          val fr = new FileReader(f)
          try {
            p.load(fr)
            result ++= p
          } finally fr.close
          cache = result
          lastModified = fLastModified
        } else cache
      case _ =>
    }
    result
  }

  def propFile : Option[JFile] = {
    val sireumHome = System.getenv("SIREUM_HOME")
    if (sireumHome != null) {
      val f = new JFile(new JFile(sireumHome), SIREUM_PROPERTIES)
      if (f.exists) {
        return Some(f)
      }
    }
    None
  }
}