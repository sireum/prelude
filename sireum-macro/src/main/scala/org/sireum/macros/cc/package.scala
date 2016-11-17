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

  val INTERNAL = System.getenv("SIREUM_INTERNAL") match {
    case "true" => true
    case _      => false
  }
  
  def it(tt : Unit) = macro itInternalImpl
  
  def it(cond : Boolean, tt : Unit) = macro itImpl

  def ite[T](tt : T, ff : T) = macro iteInternalImpl[T]
    
  def ite[T](cond : Boolean, tt : T, ff : T) = macro iteImpl[T]
  
  def iteInternalImpl[T](c : scala.reflect.macros.blackbox.Context)(
    tt : c.Expr[T], ff : c.Expr[T]) : c.Expr[T] =
    if (INTERNAL) tt else ff

  def itInternalImpl(c : scala.reflect.macros.blackbox.Context)(
      tt : c.Expr[Unit]) : c.Expr[Unit] =
    if (INTERNAL) tt else c.universe.reify {}

  def iteImpl[T](c : scala.reflect.macros.blackbox.Context)(
    cond : c.Expr[Boolean], tt : c.Expr[T], ff : c.Expr[T]) : c.Expr[T] =
    if (c.eval(c.Expr(c.untypecheck(cond.tree)))) tt else ff

  def itImpl(c : scala.reflect.macros.blackbox.Context)(
    cond : c.Expr[Boolean], tt : c.Expr[Unit]) : c.Expr[Unit] =
    if (c.eval(c.Expr(c.untypecheck(cond.tree)))) tt else c.universe.reify {}
}
