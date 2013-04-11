/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

import org.sireum.util.converter.java._

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object JavaConverter {

  def javafy(o : Any)(
    implicit seen : MIdMap[AnyRef, Object]) : Object =
    o match {
      case b : Boolean                       => boolean2Boolean(b)
      case b : Byte                          => byte2Byte(b)
      case c : Char                          => char2Character(c)
      case s : Short                         => short2Short(s)
      case i : Int                           => int2Integer(i)
      case l : Long                          => long2Long(l)
      case f : Float                         => float2Float(f)
      case d : Double                        => double2Double(d)
      case i : BigInt                        => i.bigInteger
      case ii : org.sireum.util.math.Integer => new SireumInteger(ii.toBigInt.bigInteger)
      case s : String                        => s
      case e : java.lang.Enum[_]             => e
      case null                              => null
      case None                              => ScalaOption.None
      case Some(o)                           => new ScalaOption(javafy(o))
      case o : AnyRef =>
        if (seen.contains(o)) seen(o)
        else {
          o match {
            case m : scala.collection.Map[_, _] =>
              val size = m.size
              val elements = new Array[Object](size)
              import ScalaCollectionType._
              val result =
                m match {
                  case m : ILinkedMap[_, _] =>
                    new ScalaCollection(ILinkedMap, elements)
                  case m : IMap[_, _] =>
                    new ScalaCollection(IMap, elements)
                }
              seen(o) = result
              var i = 0
              for (e <- m) {
                elements(i) = new ScalaPair(javafy(e._1), javafy(e._2))
                i += 1
              }
              result
            case t : scala.collection.Traversable[_] =>
              val size = t.size
              val elements = new Array[Object](size)
              import ScalaCollectionType._
              val result =
                t match {
                  case t : IList[_]  => new ScalaCollection(IList, elements)
                  case t : ISet[_]   => new ScalaCollection(ISet, elements)
                  case t : IStack[_] => new ScalaCollection(IStack, elements)
                }
              var i = 0
              for (e <- t) {
                elements(i) = javafy(e)
                i += 1
              }
              result
            case p : Product with PropertyProvider if !p.propertyMap.isEmpty =>
              val elementSize = p.productArity
              val elements = new Array[Object](elementSize)
              val propertySize = p.propertyMap.size
              val properties = new Array[ScalaPair](propertySize)
              val result = new ScalaProductWithProperty(p.getClass,
                elements, properties)
              seen(o) = result
              for (i <- 0 until elementSize) {
                elements(i) = javafy(p.productElement(i))
              }
              var i = 0
              for (e <- p.propertyMap) {
                properties(i) = new ScalaPair(javafy(e._1), javafy(e._2))
                i += 1
              }
              result
            case p : Product =>
              val elementSize = p.productArity
              val elements = new Array[Object](elementSize)
              val result = new ScalaProduct(p.getClass, elements)
              seen(o) = result
              for (i <- 0 until elementSize) {
                elements(i) = javafy(p.productElement(i))
              }
              result
          }
        }
    }

  def scalafy(o : Object)(implicit seen : MIdMap[Object, AnyRef]) : Any =
    o match {
      case b : java.lang.Boolean     => b
      case b : java.lang.Byte        => b
      case c : java.lang.Character   => c
      case s : java.lang.Short       => s
      case i : java.lang.Integer     => i
      case l : java.lang.Long        => l
      case f : java.lang.Float       => f
      case d : java.lang.Double      => d
      case ii : java.math.BigInteger => BigInt(ii)
      case n : SireumInteger         => org.sireum.util.math.SireumNumber(n.getValue)
      case s : String                => s
      case e : java.lang.Enum[_]     => e
      case null                      => null
      case o : ScalaOption =>
        if (o.obj == null) None
        else Some(scalafy(o.obj))
      case o : AnyRef =>
        if (seen.contains(o)) seen(o)
        else
          o match {
            case c : ScalaCollection =>
              val es = c.elements.map(scalafy)
              import ScalaCollectionType._
              import scala.collection.immutable._
              c.typ match {
                case IArray => Vector(es : _*)
                case IList  => List(es : _*)
                case ISet   => Set(es : _*)
                case IStack => Stack(es : _*)
                case ILinkedMap =>
                  ListMap(es.map { o =>
                    val sp = o.asInstanceOf[ScalaPair]
                    (sp.first, sp.second)
                  } : _*)
                case IMap =>
                  Map(es.map { o =>
                    val sp = o.asInstanceOf[ScalaPair]
                    (sp.first, sp.second)
                  } : _*)
              }
            case p : ScalaProduct =>
              val es = p.elements
              val size = es.size
              val elements = new Array[Object](size)
              for (i <- 0 until size) {
                elements(i) = scalafy(es(i)).asInstanceOf[Object]
              }
              val result = ProductUtil.make(p.pclass, elements : _*)
              seen(o) = result.asInstanceOf[Object]
              p match {
                case p : ScalaProductWithProperty =>
                  val pp = result.asInstanceOf[PropertyProvider]
                  for (sp <- p.properties) {
                    pp(sp.first) = sp.second
                    if (sp.second.isInstanceOf[PropertyProviderContext[_]])
                      sp.second.
                        asInstanceOf[PropertyProviderContext[PropertyProvider]].
                        context(pp)
                  }
                case _ =>
              }
              result
          }
    }
}