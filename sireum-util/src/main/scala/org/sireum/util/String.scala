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

  def replace(s : String, offsetReplaces : OffsetReplace*) : String = {
    var sb = new StringBuilder
    val ors = offsetReplaces.sortWith({ (or1, or2) =>
      if (or1.offsetBegin < or2.offsetBegin) {
        assert(or1.offsetEnd < or2.offsetBegin)
        true
      } else false
    })
    var i = 0
    for (or <- ors if i < s.length) {
      val begin = or.offsetBegin
      if (begin <= s.length) {
        sb.append(s.substring(i, begin))
        sb.append(or.text)
      }
      i = or.offsetEnd + 1
    }
    if (i < s.length) {
      sb.append(s.substring(i))
    }
    sb.toString
  }

  /**
   * @author <a href="mailto:robby@k-state.edu">Robby</a>
   */
  final case class OffsetReplace(
      offsetBegin : Int, offsetEnd : Int, text : String) {
    require(offsetBegin <= offsetEnd)
  }
}