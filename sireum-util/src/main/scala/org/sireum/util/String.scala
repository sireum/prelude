/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

import java.io.LineNumberReader
import java.io.StringReader

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object StringUtil {

  def readLines(s : String) : ISeq[String] = {
    val lineSep = System.lineSeparator
    val lnr = new LineNumberReader(new StringReader(s))
    var line = lnr.readLine
    var result = ivectorEmpty[String]
    while (line != null) {
      result :+= line
      line = lnr.readLine
    }
    result
  }
}