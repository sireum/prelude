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
trait BufferedUpdate[S <: BufferedUpdate[S]] extends SelfType[S] {
  private var fs = ivectorEmpty[S => S]

  protected def +>(f : S => S) : this.type = {
    fs = fs :+ f 
    this
  }

  def commit : S = {
    var r = this.asInstanceOf[S]
    for (f <- fs)
      r = f(r)
    r
  }
}