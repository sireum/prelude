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
class Exec {
  import Exec._
  import scala.actors._
  import scala.actors.Actor._

  def run(waitTime : Long, args : Seq[String], input : Option[String], dir : Option[File] = None) : Result = {
    singleReader(self, waitTime, dir) ! (args, input)

    (if (waitTime < 0)
      receive[Result] _
    else receiveWithin[Result](waitTime) _) {
      case TIMEOUT | Some(TIMEOUT) =>
        Timeout
      case e : Exception =>
        ExceptionRaised(e)
      case (result : String, exitValue : Int) =>
        StringResult(result, exitValue)
    }
  }

  private def singleReader(caller : Actor, waitTime : Long, dir : Option[File]) = actor {
    (if (waitTime < 0)
      react _
    else
      reactWithin(waitTime) _) {
      case TIMEOUT =>
        caller ! Some(TIMEOUT)
      case (args : Seq[_], in : Option[_]) =>
        import java.io._
        val processBuilder = new ProcessBuilder(args.asInstanceOf[Seq[String]] : _*)
        if (dir.isDefined)
          processBuilder.directory(dir.get)
        processBuilder.redirectErrorStream(true)
        try {
          val proc = processBuilder.start()
          if (in.isDefined) {
            val osr = new OutputStreamWriter(proc.getOutputStream)
            osr.write(in.get.asInstanceOf[String])
            osr.flush
          }
          val br = new BufferedReader(new InputStreamReader(proc.getInputStream))
          val sb = new StringBuilder()
          var line : String = null
          while ({ line = br.readLine; line != null }) {
            sb.append(line)
            sb.append('\n')
          }
          proc.waitFor
          caller ! (sb.toString, proc.exitValue)
        } catch {
          case e : Exception =>
            caller ! e
        }
    }
  }
}
