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
object Exec {
  sealed abstract class Result
  object Timeout extends Result
  case class ExceptionRaised(e : Exception) extends Result
  case class StringResult(s : String, exitValue : Int) extends Result
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
final class Exec {
  private val sb = new StringBuffer

  val env = mmapEmpty[String, String]

  def run(waitTime : Long, args : Seq[String], input : Option[String],
          extraEnv : (String, String)*) : Exec.Result =
    run(waitTime, args, input, None, extraEnv : _*)

  def run(waitTime : Long, args : Seq[String], input : Option[String],
          dir : Option[File], extraEnv : (String, String)*) : Exec.Result = {
    import scala.sys.process._
    val p = Process({
      val pb = new java.lang.ProcessBuilder(args : _*)
      pb.redirectErrorStream(true)
      dir.foreach(d => pb.directory(d))
      val m = pb.environment
      for ((k, v) <- extraEnv) {
        m.put(k, v)
      }
      pb
    }).run(new ProcessIO(inputF(input), outputF, errorF))
    
    if (waitTime <= 0) {
      val x = p.exitValue
      Exec.StringResult(sb.toString, x)
    } else {
      import scala.concurrent._
      import scala.concurrent.duration._
      import scala.concurrent.ExecutionContext.Implicits.global

      try {
        val x = Await.result(future { p.exitValue }, waitTime.millis)
        Exec.StringResult(sb.toString, x)
      } catch {
        case _ : TimeoutException =>
          p.destroy
          Exec.Timeout
      }
    }
  }

  def inputF(in : Option[String])(out : OutputStream) {
    val osw = new OutputStreamWriter(out)
    try in match {
      case Some(s) => osw.write(s, 0, s.length)
      case _       =>
    }
    finally osw.close
  }

  def outputF(is : InputStream) {
    val buffer = new Array[Byte](10 * 1024)
    try {
      var n = is.read(buffer)
      while (n != -1) {
        sb.append(new String(buffer, 0, n))
        n = is.read(buffer)
      }
    } finally is.close
  }

  def errorF(is : InputStream) {
    try while (is.read != -1) {} finally is.close
  }
}

