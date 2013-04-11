/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

import scala.collection.Map
import scala.util.control.NoStackTrace

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object PartialFunctionUtil {
  def empty[A, B] = Map[A, B]()

  def default[A, B](b : B) : A --> B = {
    case _ => b
  }

  def toPartialFunction[K, V](f : K => V) =
    new PartialFunction[K, V] {
      def apply(k : K) = f(k)
      def isDefinedAt(k : K) = true
    }

  val EMPTY_MAP = Map()

  def isEmpty(f : _ --> _) = f == EMPTY_MAP

  def orElse[A, B, A1 <: A, B1 >: B] //
  (f1 : A --> B, f2 : A1 --> B1) : A1 --> B1 =
    if (isEmpty(f1)) f2
    else if (isEmpty(f2)) f1
    else f1 orElse f2

  def orElses[A, B](fs : (A --> B)*) : A --> B = orElses(fs)
    
  def orElses[A, B](fs : Iterable[A --> B]) : A --> B =
    new PartialFunction[A, B] {
      lazy val pfs = {
        val r = marrayEmpty[A --> B]
        for (f <- fs)
          if (!isEmpty(f))
            r += f
        r
      }
      def apply(a : A) : B = {
        for (f <- pfs)
          if (f isDefinedAt a) return f(a)
        sys.error("Undefined operation for input: " + a)
      }

      def isDefinedAt(a : A) : Boolean = {
        for (f <- pfs)
          if (f isDefinedAt a) return true
        false
      }
    }

  def chain[A, B, A1 <: A, B1 >: B] //
  (f1 : A --> B, f2 : A1 --> B1) : A1 --> B1 =
    if (isEmpty(f1)) f2
    else
      new PartialFunction[A1, B1] {
        def apply(a : A1) =
          if (f1 isDefinedAt a)
            try { f1(a) }
            catch {
              case ex : ChainUndefined =>
                if (f2 isDefinedAt a) f2(a)
                else throw ex
            }
          else f2(a)

        def isDefinedAt(a : A1) =
          (f1 isDefinedAt a) || (f2 isDefinedAt a)
      }

  final class ChainUndefined extends NoStackTrace
}