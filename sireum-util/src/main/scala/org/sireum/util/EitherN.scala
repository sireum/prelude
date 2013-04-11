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
sealed abstract class Either3[+T1, +T2, +T3]

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object Either3 {
  final case class First[+T1, +T2, +T3](value : T1) extends Either3[T1, T2, T3]
  final case class Second[+T1, +T2, +T3](value : T2) extends Either3[T1, T2, T3]
  final case class Third[+T1, +T2, +T3](value : T3) extends Either3[T1, T2, T3]
}