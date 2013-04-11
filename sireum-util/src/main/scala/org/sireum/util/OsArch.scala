/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object OsArchUtil {
  def detect : OsArch.Type = {
    import OsArch._

    val is64bit = System.getProperty("os.arch").contains("64")

    val osName = System.getProperty("os.name").toLowerCase()
    if (osName.indexOf("mac") >= 0)
      (if (is64bit) Mac64 else Mac32)
    else if (osName.indexOf("nux") >= 0)
      (if (is64bit) Linux64 else Linux32)
    else if (osName.indexOf("win") >= 0)
      (if (is64bit) Win64 else Win32)
    else
      Unsupported
  }
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object OsArch extends Enum {
  sealed abstract class Type extends EnumElem
  object Unsupported extends Type
  object Mac32 extends Type
  object Mac64 extends Type
  object Linux32 extends Type
  object Linux64 extends Type
  object Win32 extends Type
  object Win64 extends Type

  def elements = ivector(Mac32, Mac64, Linux32, Linux64, Win32, Win64)
}