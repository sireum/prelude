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
import java.io.Writer
import org.apache.commons.lang3.StringEscapeUtils

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
        assert(or1.offsetBegin + or1.length < or2.offsetBegin + or2.length)
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
      i = or.offsetBegin + or.length
    }
    if (i < s.length) {
      sb.append(s.substring(i))
    }
    sb.toString
  }

  def insertHtml(w : Writer, s : String, replace : Char --> String,
                 offsetInserts : OffsetInsert*) {
      def appendw(c : Char) {
        if (replace isDefinedAt c)
          w.append(replace(c))
        else
          w.append(StringEscapeUtils.escapeHtml4(c.toString))
      }
    insert(w, s, appendw, offsetInserts : _*)
  }

  def insert(w : Writer, s : String, appendw : Char => Unit,
             offsetInserts : OffsetInsert*) {
    val a = offsetInserts.toArray
    scala.util.Sorting.quickSort(a)
    val numInserts = a.length
    var j = 0
    var stop = false
    for (i <- 0 until s.length if !stop)
      if (j == numInserts) {
        s.substring(i).foreach(appendw)
        stop = true
      } else {
        var skip = false
        while (j < numInserts && !skip) {
          val of = a(j)
          if (of.offset == i) {
            w.append(of.text)
            j += 1
          } else skip = true
        }
        appendw(s.charAt(i))
      }
    while (j < numInserts) {
      val of = a(j)
      if (of.offset == s.length) {
        w.append(of.text)
      }
      j += 1
    }
  }

  /**
   * @author <a href="mailto:robby@k-state.edu">Robby</a>
   */
  final case class OffsetReplace(
      offsetBegin : Int, length : Int, text : String) {
  }

  /**
   * @author <a href="mailto:robby@k-state.edu">Robby</a>
   */
  final case class OffsetInsert(
    offset : Int, z : Int, text : String)
      extends Ordered[OffsetInsert] {
    def compare(that : OffsetInsert) =
      if (offset < that.offset) -1
      else if (offset == that.offset) z.compare(that.z)
      else 1
  }
}