/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util.sexp.ast

import java.io._
import org.antlr.runtime._
import org.antlr.runtime.tree.Tree
import org.sireum.util._
import org.sireum.util.sexp.parser._

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object SExprAst {

  def build(f : File) : ListSExpr = build(new ANTLRFileStream(f.getAbsolutePath))
  def build(s : String) : ListSExpr = build(new ANTLRStringStream(s))

  def build(stream : ANTLRStringStream) : ListSExpr = {
    val lexer = new SExprLexer(stream)
    val parser = new SExprParser(new CommonTokenStream(lexer))
    val v = new Visitor
    v.visit(parser.sexps.getTree.asInstanceOf[Tree])
    v.pop.asInstanceOf[ListSExpr]
  }

  class Visitor extends SExprTreeVisitor {
    import scala.collection.JavaConversions._

    var result : Option[SExprAstNode] = None

    def push(n : SExprAstNode) {
      require(result.isEmpty)
      result = Some(n)
    }

    def pop = {
      require(result.isDefined)
      val n = result.get
      result = None
      n
    }

    override def visitEXP(t : Tree) = visitPEXP(t)

    override def visitPEXP(t : Tree) = {
      val size = t.getChildCount
      var l = ivectorEmpty[SExprAstNode]
      for (i <- 0 until size) {
        visit(t.getChild(i))
        val e = pop
        l = l :+ e
      }
      push(ListSExpr(l))
      false
    }

    override def visitATOM(t : Tree) = {
      push(AtomSExpr(t.getText))
      false
    }
  }
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
sealed abstract class SExprAstNode

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
case class ListSExpr(children : ISeq[SExprAstNode]) extends SExprAstNode

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
case class AtomSExpr(text : String) extends SExprAstNode