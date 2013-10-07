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
final case class IdentityContainer[T <: AnyRef](o : T) {
  override def hashCode = System.identityHashCode(o)

  override def equals(other : Any) : Boolean =
    other match {
      case other : IdentityContainer[_] => o eq other.o
      case _                            => false
    }
}
