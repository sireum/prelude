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
package object cc {
  import scala.language.experimental.macros

  def ite[T](cond : Boolean, tt : T, ff : T) = macro iteImpl[T]

  def iteImpl[T](c : scala.reflect.macros.Context)(
    cond : c.Expr[Boolean], tt : c.Expr[T], ff : c.Expr[T]) : c.Expr[T] =
    if (c.eval(c.Expr(c.resetAllAttrs(cond.tree)))) tt else ff

  def it(cond : Boolean, tt : Unit) = macro itImpl

  def itImpl(c : scala.reflect.macros.Context)(
    cond : c.Expr[Boolean], tt : c.Expr[Unit]) : c.Expr[Unit] =
    if (c.eval(c.Expr(c.resetAllAttrs(cond.tree)))) tt else c.universe.reify {}
}
