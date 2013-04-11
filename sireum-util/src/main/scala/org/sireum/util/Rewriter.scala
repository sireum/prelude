/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

import scala.annotation.tailrec

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object Rewriter {
  object TraversalMode extends Enum {
    sealed abstract class Type extends EnumElem
    object TOP_DOWN extends Type
    object BOTTOM_UP extends Type

    def elements = ivector(TOP_DOWN, BOTTOM_UP)
  }

  def all(f : RewriteFunction, g : RewriteFunction) : RewriteFunction =
    new PartialFunction[Any, Any] {
      def isDefinedAt(o : Any) = true
      def apply(o : Any) = {
        val o1 = if (f.isDefinedAt(o)) f(o) else o
        if (g.isDefinedAt(o1)) g(o1) else o1
      }
    }

  def all(fs : ISeq[RewriteFunction]) : RewriteFunction =
    new PartialFunction[Any, Any] {
      def isDefinedAt(o : Any) = true
      def apply(o : Any) = {
        var r = o
        for (f <- fs) {
          if (f.isDefinedAt(r))
            r = f(r)
        }
        r
      }
    }

  def build[T](f : RewriteFunction,
               mode : TraversalMode.Type = TraversalMode.TOP_DOWN) =
    { x : T =>
      mode match {
        case TraversalMode.TOP_DOWN =>
          rewrite(Some({ _ => f }), None)(x)
        case TraversalMode.BOTTOM_UP =>
          rewrite(None, Some({ _ => f }))(x)
      }
    }

  def buildEnd[T](f : RewriteFunction, g : RewriteFunction,
                  mode : TraversalMode.Type = TraversalMode.TOP_DOWN) =
    { x : T =>
      mode match {
        case TraversalMode.TOP_DOWN =>
          rewrite(Some({ _ => f }), Some({ _ => g }))(x)
        case TraversalMode.BOTTOM_UP =>
          rewrite(None, Some({ _ => all(f, g) }))(x)
      }
    }

  import Visitor._

  private[util] trait RewritableStackElement[T] {
    def newChildren : Array[Object]
    var isDirty : Boolean
    def makeWithNewChildren : T

    def newChild(i : Int, oPrevious : Any, o : Any) {
      isDirty = isDirty || !same(oPrevious, o)
      newChildren(i) = o.asInstanceOf[Object]
    }

    private def same(v1 : Any, v2 : Any) : Boolean =
      (v1, v2) match {
        case (v1 : Product2[_, _], v2 : Product2[_, _]) =>
          same(v1._1, v2._1) && same(v1._2, v2._2)
        case (v1 : AnyRef, v2 : AnyRef) => v1 eq v2
        case _                          => v1 == v2
      }
  }

  private[util] class RTraversableStackElement(
    value : scala.collection.Traversable[_], alwaysCopy : Boolean,
    r : Any => Any)
      extends TraversableStackElement(value)
      with RewritableStackElement[scala.collection.Traversable[_]] {
    val newChildren = new Array[Object](value.size)
    var isDirty = alwaysCopy

    import scala.language.higherKinds
    import scala.collection.generic.CanBuildFrom

    def makeWithNewChildren =
      if (isDirty)
        value match {
          case m : ILinkedMap[_, _] =>
            makeHelperM(value.asInstanceOf[ILinkedMap[Any, Any]])
          case m : scala.collection.Map[_, _] =>
            makeHelperM(value.asInstanceOf[scala.collection.Map[Any, Any]])
          case t : IVector[_] =>
            makeHelperT(value.asInstanceOf[IVector[Any]])
          case _ =>
            makeHelperT(value.asInstanceOf[scala.collection.Traversable[Any]])
        }
      else value

    private def makeHelperT[CC[_] <: scala.collection.Traversable[Any]](t : CC[Any]) //
    (implicit cbf : CanBuildFrom[CC[Any], Any, CC[Any]]) : CC[Any] = {
      val b = cbf(t)
      b.sizeHint(t.size)
      b ++= newChildren
      b.result
    }

    private def makeHelperM[CC[V, W] <: scala.collection.Map[V, W]](t : CC[Any, Any]) //
    (implicit cbf : CanBuildFrom[CC[Any, Any], (Any, Any), CC[Any, Any]]) : CC[Any, Any] = {
      val b = cbf(t)
      b.sizeHint(t.size)
      b ++= newChildren.map(_.asInstanceOf[(Any, Any)])
      b.result
    }
  }

  private[util] class RProductStackElement(
    value : Product, alwaysCopy : Boolean)
      extends ProductStackElement(value : Product)
      with RewritableStackElement[Product] {
    val newChildren = new Array[Object](value.productArity)
    var isDirty = alwaysCopy

    def makeWithNewChildren =
      if (isDirty)
        ProductUtil.make(value.getClass, newChildren : _*)
      else value
  }

  private[util] class RVisitableStackElement(
    override val value : Rewritable, alwaysCopy : Boolean)
      extends VisitableStackElement(value)
      with RewritableStackElement[Rewritable] {
    val newChildren = new Array[Object](value.getNumOfChildren)
    var isDirty = alwaysCopy

    def makeWithNewChildren =
      if (isDirty) value.make(newChildren : _*) else value
  }

  def rewrite[T](fnPre : Option[VisitorStackProvider => RewriteFunction],
                 fnPost : Option[VisitorStackProvider => RewriteFunction],
                 alwaysCopy : Boolean = false)(o : T) : T = {

    var _stack = ilistEmpty[VisitorStackElementRoot with RewritableStackElement[_]]

    val vsp = new VisitorStackProvider {
      def stack = _stack
    }

    val (hasPre, f) =
      if (fnPre.isDefined) (true, fnPre.get(vsp)) else (false, null)
    val (hasPost, g) =
      if (fnPost.isDefined) (true, fnPost.get(vsp)) else (false, null)

      @inline
      def peek = _stack.head

      def pop = {
        val oldV = peek.value
        val v = peek.makeWithNewChildren
        var result =
          if (hasPost && g.isDefinedAt(v)) (oldV, g(v)) else (oldV, v)
        _stack = _stack.tail
        result
      }

    var result : Any = o

      @tailrec
      def isEmpty : Boolean = {
        if (_stack.isEmpty) true
        else if (_stack.head.hasNext) false
        else {
          var p = pop
          if (_stack.isEmpty) result = p._2
          else peek.newChild(peek.currIndex, p._1, p._2)
          isEmpty
        }
      }

      def rewriter(o : Any) : Any = {
          def push(o : Any) {
            o match {
              case t : scala.collection.Traversable[_] =>
                _stack = new RTraversableStackElement(t, alwaysCopy, rewriter _) :: _stack
              case p : Product =>
                _stack = new RProductStackElement(p, alwaysCopy) :: _stack
              case v : Rewritable =>
                _stack = new RVisitableStackElement(v, alwaysCopy) :: _stack
              case _ =>
            }
          }

          def add(n : Any) {
            val r = if (hasPre && f.isDefinedAt(n)) f(n) else n
            if (_stack.isEmpty) push(r)
            else {
              peek.newChild(peek.currIndex, n, r)
              push(r)
            }
          }

        add(o)
        while (!isEmpty)
          add(peek.next)
        result
      }

    rewriter(o).asInstanceOf[T]
  }
}