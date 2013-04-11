/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

import java.io._

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object RstUtil {
  def lines(s : String) = {
    val lnr = new LineNumberReader(new StringReader(s))
    var line = lnr.readLine
    var result = ivectorEmpty[String]
    while (line != null) {
      result = result :+ line
      line = lnr.readLine
    }
    result
  }

  def rep(sb : StringBuilder, c : Char, n : Int) {
    for (i <- 0 until n) {
      sb.append(c)
    }
  }

  def rep(c : Char, n : Int) : String = {
    val sb = new StringBuilder
    rep(sb, c, n)
    sb.toString
  }

  def tab(s1 : String, s2 : String) = {
    var lines1 = lines(s1)
    var lines2 = lines(s2)
    val maxLines = {
      l : Traversable[String] => l.foldLeft(0)((n, s) => max(n, s.length))
    }
    val maxLength = max(maxLines(lines1), maxLines(lines2))
    while (lines1.size > lines2.size) {
      lines2 = lines2 :+ ""
    }
    while (lines1.size < lines2.size) {
      lines1 = lines1 :+ ""
    }
    val lineSep = System.lineSeparator
    val sb = new StringBuilder
    val padLength = maxLength + 2
    sb.append('+')
    rep(sb, '-', padLength)
    sb.append('+')
    rep(sb, '-', padLength)
    sb.append('+')
    sb.append(lineSep)
    sb.append('|')
    rep(sb, ' ', padLength)
    sb.append('|')
    rep(sb, ' ', padLength)
    sb.append('|')
    sb.append(lineSep)
    for ((line1, line2) <- lines1.zip(lines2)) {
      sb.append("| ")
      sb.append(line1)
      rep(sb, ' ', maxLength - line1.length)
      sb.append(" | ")
      sb.append(line2)
      rep(sb, ' ', maxLength - line2.length)
      sb.append(" |")
      sb.append(lineSep)
    }
    sb.append('|')
    rep(sb, ' ', padLength)
    sb.append('|')
    rep(sb, ' ', padLength)
    sb.append('|')
    sb.append(lineSep)
    sb.append('+')
    rep(sb, '-', padLength)
    sb.append('+')
    rep(sb, '-', padLength)
    sb.append('+')
    sb.append(lineSep)
  }
}