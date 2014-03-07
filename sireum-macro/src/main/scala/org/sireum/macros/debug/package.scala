/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/
package org.sireum.macros

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
package object debug {
  val DEBUG = System.getProperty("SIREUM_DEBUG") match {
    case "true" => true
    case _      => false
  }

  import scala.language.experimental.macros

  def assert(
    cond : Boolean,
    msg : Lazy[String]) : Unit = macro assertDebugImpl

  def assert(
    enableCond : Boolean,
    cond : Boolean,
    msg : Lazy[String]) : Unit = macro assertImpl

  def assertDebugImpl(c : scala.reflect.macros.Context)(
    cond : c.Expr[Boolean], msg : c.Expr[Lazy[String]]) : c.Expr[Unit] = {
    import c.universe._

    if (DEBUG)
      reify {
        if (!cond.splice)
          throw new AssertionError(
            s"Assertion violated: ${msg.splice.value}")
      }
    else
      reify {}
  }

  def assertImpl(c : scala.reflect.macros.Context)(
    enableCond : c.Expr[Boolean], cond : c.Expr[Boolean],
    msg : c.Expr[Lazy[String]]) : c.Expr[Unit] = {
    import c.universe._

    if (c.eval(c.Expr(c.resetLocalAttrs(enableCond.tree))))
      reify {
        if (!cond.splice)
          throw new AssertionError(
            s"Assertion violated: ${msg.splice.value}")
      }
    else
      reify {}
  }

  import language.implicitConversions

  implicit def toLazyString(t : String) : Lazy[String] = new Lazy(t)
}
