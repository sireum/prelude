/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

import scala.annotation.tailrec
import com.google.common.base.Optional

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object Visitor {
  object TraversalMode extends Enum {
    sealed abstract class Type extends EnumElem
    object TOP_DOWN extends Type
    object BOTTOM_UP extends Type

    def elements = ivector(TOP_DOWN, BOTTOM_UP)
  }

  def map(fs : ISeq[VisitorFunction],
          parallel : Boolean = false) : VisitorFunction = {
    case x : Any =>
      (if (parallel) fs.par else fs).
        map({ f => if (f isDefinedAt x) f(x) else true }).
        foldLeft(false)((x, y) => x || y)
  }

  def atMostOne(fs : ISeq[VisitorFunction],
                parallel : Boolean = false) : VisitorFunction = {
    case x : Any if (if (parallel) fs.par else fs).exists(_.isDefinedAt(x)) =>
      val rs = (if (parallel) fs.par else fs).
        map({ f => if (f isDefinedAt x) (true, f(x)) else (false, false) })
      var result = false
      var found = false
      for (r <- rs) {
        if (r._1) {
          assert(!found)
          found = true
          result = r._2
        }
      }
      result
  }

  def first(fs : ISeq[VisitorFunction],
            parallel : Boolean = false) : VisitorFunction = {
    case x : Any if (if (parallel) fs.par else fs).exists(_.isDefinedAt(x)) =>
      val size = fs.size
      var i = 0;
      var found = false
      var result = false
      while (i < size && !found) {
        val f = fs(i)
        if (f.isDefinedAt(x)) {
          found = true
          result = f(x)
        }
        i += 1
      }
      result
  }

  def seq1(f : VisitorFunction, g : VisitorFunction) =
    new PartialFunction[Any, Boolean] {
      def isDefinedAt(o : Any) = f.isDefinedAt(o) || g.isDefinedAt(o)
      def apply(o : Any) = {
        var result = if (f.isDefinedAt(o)) f(o) else true
        if (g.isDefinedAt(o)) g(o)
        result
      }
    }

  def build(f : VisitorFunction,
            mode : TraversalMode.Type = TraversalMode.TOP_DOWN) =
    { x : Any =>
      mode match {
        case TraversalMode.TOP_DOWN  => visit(Some({ _ => f }))(x)
        case TraversalMode.BOTTOM_UP => visit(None, Some({ _ => f }))(x)
      }
    }

  def buildEnd(f : VisitorFunction, g : VisitorFunction,
               mode : TraversalMode.Type = TraversalMode.TOP_DOWN) =
    { x : Any =>
      mode match {
        case TraversalMode.TOP_DOWN  => visit(Some({ _ => f }), Some({ _ => g }))(x)
        case TraversalMode.BOTTOM_UP => visit(None, Some({ _ => seq1(f, g) }))(x)
      }
    }

  trait VisitorStackProvider {
    def stack : IList[VisitorStackElementRoot]
  }

  trait VisitorStackElement {
    def value : Any
  }

  private[util] abstract class VisitorStackElementRoot extends VisitorStackElement {
    def hasNext : Boolean
    def next : Any
    def nextIndex : Int
    def currIndex = nextIndex - 1
    override def toString = "[" + currIndex + ", " + value.toString + "]"
  }

  private[util] class TraversableStackElement(val value : scala.collection.Traversable[_])
      extends VisitorStackElementRoot {
    var nextIndex = 0
    var curr : Any = null
    val it = value.toIterator
    def hasNext = it.hasNext
    def next = { curr = it.next; nextIndex += 1; curr }
  }

  private[util] class IterableStackElement(val value : java.lang.Iterable[_])
      extends VisitorStackElementRoot {
    var nextIndex = 0
    var curr : Any = null
    val it = value.iterator
    def hasNext = it.hasNext
    def next = { curr = it.next; nextIndex += 1; curr }
  }

  private[util] class ProductStackElement(val value : Product)
      extends VisitorStackElementRoot {
    var nextIndex = 0
    var curr : Any = null
    def hasNext = nextIndex < value.productArity
    def next = { curr = value.productElement(nextIndex); nextIndex += 1; curr }
  }

  private[util] class VisitableStackElement(val value : Visitable)
      extends VisitorStackElementRoot {
    var nextIndex = 0
    var curr : Any = null
    val children = value.getChildren
    def hasNext = nextIndex < value.getNumOfChildren
    def next = { curr = children(nextIndex); nextIndex += 1; curr }
  }

  def visit(fnPre : Option[VisitorStackProvider => VisitorFunction],
            fnPost : Option[VisitorStackProvider => VisitorFunction] = None) //
            (o : Any) : Boolean = {
    var _stack = ilistEmpty[VisitorStackElementRoot]

    val vsp = new VisitorStackProvider {
      def stack = _stack
    }

    val (hasPre, f) =
      if (fnPre.isDefined) (true, fnPre.get(vsp)) else (false, null)
    val (hasPost, g) =
      if (fnPost.isDefined) (true, fnPost.get(vsp)) else (false, null)

    require(hasPre || hasPost)

      def push(o : Any) {
        o match {
          case t : scala.collection.Traversable[_] =>
            _stack = new TraversableStackElement(t) :: _stack
          case p : Product =>
            _stack = new ProductStackElement(p) :: _stack
          case v : Visitable =>
            _stack = new VisitableStackElement(v) :: _stack
          case i : java.lang.Iterable[_] =>
            _stack = new IterableStackElement(i) :: _stack
          case o : Optional[_] =>
            _stack = new ProductStackElement(
              if (o.isPresent) Some(o.get) else None) :: _stack
          case _ =>
        }
      }

      @inline
      def peek = _stack.head

      def pop {
        if (hasPost && g.isDefinedAt(peek.value))
          g(peek.value)
        _stack = _stack.tail
      }

      @tailrec
      def isEmpty : Boolean = {
        if (_stack.isEmpty) true
        else if (_stack.head.hasNext) false
        else {
          pop
          isEmpty
        }
      }

    var result = true

      def add(n : Any) {
        if (hasPre && f.isDefinedAt(n)) {
          if (f(n))
            push(n)
          else result = false
        } else push(n)
      }

    add(o)
    while (!isEmpty)
      add(peek.next)
    result
  }
}