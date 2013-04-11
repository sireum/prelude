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
object FixedPoint {
  def fix[T](m : MMap[T, MSet[T]]) : Unit = {
    val workList = mlistEmpty[T] ++ m.keys
    val emptyset = msetEmpty[T]
    while (!workList.isEmpty) {
      val i = workList.remove(0)
      val is = m(i)
      val size = is.size
      is ++= (is.flatMap { m.get(_).getOrElse(emptyset) })
      if (size != is.size)
        workList += i
    }
  }
}