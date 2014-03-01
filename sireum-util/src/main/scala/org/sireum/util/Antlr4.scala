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
object Antlr4 {
  import org.antlr.v4.runtime._
  import org.antlr.v4.runtime.tree._

  trait Visitor[T <: PropertyProvider] extends ParseTreeVisitor[T] {
    def getChildren[T, PT <: ParseTree](
      trees : java.util.List[PT]) : ISeq[T] = {
      var children = ivectorEmpty[T]
      if (trees != null) {
        import scala.collection.JavaConversions._
        for (tree <- trees)
          children :+= getChild(tree)
      }
      children
    }

    def getChild[T](tree : ParseTree) : T =
      visit(tree).asInstanceOf[T]

    def getOptChild[T](tree : ParseTree) : Option[T] =
      if (tree == null) None
      else Some(visit(tree).asInstanceOf[T])
  }

  implicit class Location[T <: PropertyProvider](
      val node : T) extends AnyVal {
    import org.sireum.util.{ Location => L }

    def at(ctx : ParserRuleContext, locPropKey : String = L.locPropKey)(
      implicit source : Option[FileResourceUri], enabled : Boolean) : T = {
      val start = ctx.start
      val stop = if (ctx.stop == null) ctx.start else ctx.stop
      at(start, stop, locPropKey)(source, enabled)
    }

    def at(tn : org.antlr.v4.runtime.tree.TerminalNode)(
      implicit source : Option[FileResourceUri], enabled : Boolean) : T =
      at(tn, L.locPropKey)(source, enabled)

    def at(tn : org.antlr.v4.runtime.tree.TerminalNode,
           locPropKey : String)(
             implicit source : Option[FileResourceUri], enabled : Boolean) : T =
      at(tn.getSymbol, locPropKey)(source, enabled)

    def at(tnBegin : org.antlr.v4.runtime.tree.TerminalNode,
           tnEnd : org.antlr.v4.runtime.tree.TerminalNode)(
             implicit source : Option[FileResourceUri], enabled : Boolean) : T =
      at(tnBegin, tnEnd, L.locPropKey)(source, enabled)

    def at(tnBegin : org.antlr.v4.runtime.tree.TerminalNode,
           tnEnd : org.antlr.v4.runtime.tree.TerminalNode,
           locPropKey : String)(
             implicit source : Option[FileResourceUri], enabled : Boolean) : T =
      at(tnBegin.getSymbol, tnEnd.getSymbol, locPropKey)(source, enabled)

    def at(start : Token, stop : Token)(
      implicit source : Option[FileResourceUri], enabled : Boolean) : T =
      at(start, stop, L.locPropKey)(source, enabled)

    def at(start : Token, stop : Token, locPropKey : String)(
      implicit source : Option[FileResourceUri], enabled : Boolean) : T =
      if (!enabled) node
      else {
        val lb = stop.getLine
        val cb = stop.getCharPositionInLine
        val (le, ce) = end(lb, cb, stop.getText)
        SourceOffsetLocation.At.pp2sol(node)(locPropKey).at(
          fileUri = source,
          offset = start.getStartIndex,
          length = stop.getStopIndex - start.getStartIndex + 1,
          lineBegin = start.getLine,
          columnBegin = start.getCharPositionInLine,
          lineEnd = le,
          columnEnd = ce)
        node
      }

    def at(t : Token)(
      implicit source : Option[FileResourceUri], enabled : Boolean) : T =
      at(t, t, L.locPropKey)(source, enabled)

    def at(t : Token, locPropKey : String)(
      implicit source : Option[FileResourceUri], enabled : Boolean) : T =
      at(t, t, locPropKey)(source, enabled)

    private def end(
      lineBegin : Int, columnBegin : Int, text : String) : (Int, Int) = {
      val i = text.lastIndexOf('\n')
      if (i < 0)
        (lineBegin, columnBegin + text.length - 1)
      else {
        var lines = 0
        for (c <- text if c == '\n') lines += 1
        (lineBegin + lines, text.length - i - 3)
      }
    }
  }
}
