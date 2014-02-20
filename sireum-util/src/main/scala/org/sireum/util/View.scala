/*
Copyright (c) 2014 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object View {
  @inline
  def usingF[U, R](u : U)(f :U => R) : R =
    f(u)

  @inline
  def using[U](u : U)(f : U => Unit) : Unit =
    f(u)

  @inline
  def foreach[U](u : U)(f : U => Unit) {
    f(u)
  }
}