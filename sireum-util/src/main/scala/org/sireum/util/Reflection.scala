/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

import java.io.File
import java.net.URLClassLoader
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object Reflection {
  import scala.reflect.runtime.universe._

  final val classLoader = {
    val cl = getClass.getClassLoader
    if (cl == null) ClassLoader.getSystemClassLoader else cl
  }

  final val mirror = runtimeMirror(classLoader)

  final val booleanType = typeOf[Boolean]
  final val charType = typeOf[Character]
  final val shortType = typeOf[Short]
  final val intType = typeOf[Int]
  final val longType = typeOf[Long]
  final val floatType = typeOf[Float]
  final val doubleType = typeOf[Double]
  final val bigIntType = typeOf[BigInt]
  final val integerType = typeOf[org.sireum.util.math.Integer]
  final val stringType = typeOf[String]
  
  def typeCheck(t : Tree, m : Mirror = mirror) : Tree = {
    import scala.tools.reflect.ToolBox

    val tb = m.mkToolBox()
    tb.typecheck(t)
  }

  def eval[T](expr : String, m : Mirror = mirror) = {
    import scala.tools.reflect.ToolBox

    val tb = m.mkToolBox()
    tb.eval(tb.parse(expr)).asInstanceOf[T]
  }

  def evalExpr[T](expr : Expr[T], m : Mirror = mirror) = {
    import scala.tools.reflect.ToolBox

    val tb = m.mkToolBox()
    tb.eval(expr.tree).asInstanceOf[T]
  }

  def parse(expr : String, m : Mirror = mirror) : Tree = {
    import scala.tools.reflect.ToolBox

    val tb = m.mkToolBox()
    tb.parse(expr)
  }

  def ast(expr : String, m : Mirror = mirror) : Tree = {
    import scala.tools.reflect.ToolBox

    val tb = m.mkToolBox()
    tb.typecheck(tb.parse(expr))
  }

  def reify[T](expr : String, m : Mirror = mirror) : T = {
    import scala.tools.reflect.ToolBox

    eval[T](s"{ import scala.reflect.runtime.universe._; reify { $expr } }", m)
  }

  @inline
  def classMirror(c : Class[_], m : Mirror = mirror) : ClassMirror = {
    m.reflectClass(m.classSymbol(c))
  }

  @inline
  def companion[T](
    c : Class[T], processAnnotations : Boolean,
    m : Mirror = mirror) : Option[(Symbol, Any, ISeq[Annotation])] = {
    val classSymbol = m.classSymbol(c)
    val companionSymbol = classSymbol.companion
    if (companionSymbol.isModule) {
      val moduleSymbol = companionSymbol.asModule
      val moduleMirror = m.reflectModule(moduleSymbol)
      try
        Some((companionSymbol, moduleMirror.instance,
          if (processAnnotations)
            moduleSymbol.annotations.toVector.map(annotation(_, m))
          else ivectorEmpty))
      catch {
        case e : ClassNotFoundException => None
      }
    } else None
  }

  @inline
  def fullName(t : Type) = t.typeSymbol.fullName

  @inline
  def constructor(t : Type) = t.decl(termNames.CONSTRUCTOR).asMethod

  @inline
  def getType(o : Any, m : Mirror = mirror) : Type = {
    val os = m.reflect(o).symbol
    os.asType.toType
  }

  @inline
  def getTypeOfClass(c : Class[_], m : Mirror = mirror) : Type =
    m.classSymbol(c).toType

  @inline
  def getClassOfType(t : Type, m : Mirror = mirror) : Class[_] =
    m.runtimeClass(t.typeSymbol.asClass)

  def classInits(tipe : Type, obj : Any,
                 includeInherited : Boolean,
                 m : Mirror = mirror) : IMap[String, Object] = {
    var result = imapEmpty[String, Object]
    for (
      d <- (if (includeInherited) tipe.members else tipe.decls) // 
      if d.isTerm && (d.asTerm.isVal || d.asTerm.isVar)
    ) {
      val name = d.name.decodedName.toString.trim
      val im = m.reflect(obj)
      val value = im.reflectField(d.asTerm).get.asInstanceOf[Object]
      result += (name -> value)
    }
    result
  }

  def traitInits(clazz : Class[_]) : IMap[String, Object] = {
    var cl = clazz.getClassLoader
    if (cl == null) {
      cl = ClassLoader.getSystemClassLoader
    }

    var init : Option[java.lang.reflect.Method] = None
    try {
      for (m <- cl.loadClass(clazz.getName + "$class").getMethods if init.isEmpty)
        if (m.getName == "$init$")
          init = Some(m)
    } catch {
      case e : Exception =>
    }

    if (init.isEmpty) return imapEmpty

    val encodedToDecodedSetterNameMap = mmapEmpty[String, String]

    for (m <- clazz.getDeclaredMethods()) {
      val setterPrefix = clazz.getName.replace('.', '$') + "$_setter_$"
      val encoded = m.getName
      if (encoded.startsWith(setterPrefix)) {
        val decoded = encoded.substring(setterPrefix.length, encoded.length - 4)
        encodedToDecodedSetterNameMap(encoded) = decoded
      }
    }

    var result = imapEmpty[String, Object]

    init.get.invoke(null, Proxy.newProxyInstance(cl, Array[Class[_]](clazz),
      new InvocationHandler {
        def invoke(proxy : Object,
                   method : java.lang.reflect.Method,
                   args : Array[Object]) = {
          encodedToDecodedSetterNameMap.get(method.getName) match {
            case Some(decodedName) => result += (decodedName -> args(0))
            case _                 =>
          }
          null
        }
      }))

    result
  }

  def annotation(
    a : scala.reflect.runtime.universe.Annotation,
    m : Mirror = mirror) : Annotation = {
    require(a.scalaArgs.isEmpty)

    val clazz = getClassOfType(a.tree.tpe)
    var args = ivectorEmpty[AnnotationArg]
    for (arg <- a.javaArgs)
      arg match {
        case (n, a2 : scala.reflect.runtime.universe.Annotation) =>
          args :+= AnnotationArg(n.decodedName.toString, annotation(a2))
        case (n, arg) =>
          args :+= AnnotationArg(n.decodedName.toString, annArgument(arg, m))
      }

    assert(classOf[java.lang.annotation.Annotation].isAssignableFrom(clazz))
    Annotation(clazz.asInstanceOf[Class[java.lang.annotation.Annotation]], args)
  }

  private def annArgument(arg : JavaArgument, m : Mirror) : Any = {
    arg match {
      case ArrayArgument(a) =>
        a.map(annArgument(_, m))
      case LiteralArgument(Constant(v : Type)) =>
        m.runtimeClass(v)
      case LiteralArgument(Constant(v : Symbol)) =>
        val c = v.owner.asClass
        m.runtimeClass(c).getDeclaredField(v.name.toString).get(null)
      case LiteralArgument(Constant(v)) =>
        v
    }
  }

  /**
   * @author <a href="mailto:robby@k-state.edu">Robby</a>
   */
  case class Annotation(
    clazz : Class[java.lang.annotation.Annotation],
    params : ISeq[AnnotationArg])

  /**
   * @author <a href="mailto:robby@k-state.edu">Robby</a>
   */
  case class AnnotationArg(
    name : String,
    value : Any)

  /**
   * @author <a href="mailto:robby@k-state.edu">Robby</a>
   */
  case class CaseClass(
      className : String,
      tipe : Type,
      annotations : ISeq[Reflection.Annotation],
      private[Reflection] var _params : ISeq[CaseClass.Param],
      private[Reflection] var _properties : IMap[Any, Any]) {
    def params = _params
    def properties = _properties
  }

  /**
   * @author <a href="mailto:robby@k-state.edu">Robby</a>
   */
  object CaseClass {
    val caseClassCache : MMap[Class[_], CaseClass] = {
      import scala.collection.JavaConversions._
      new java.util.WeakHashMap[Class[_], CaseClass]()
    }

    @inline
    def newCaseClass[T](c : Class[T], args : Object*) : T =
      ProductUtil.make(c, args : _*)

    @inline
    def caseClassType(
      c : Class[_], processAnnotations : Boolean,
      m : Mirror = mirror) : CaseClass =
      caseClassCache.getOrElseUpdate(c,
        caseClassType(getTypeOfClass(c), processAnnotations, m))

    private def caseClassType(
      tipe : Type, processAnnotations : Boolean, m : Mirror) : CaseClass = {
      val ts = tipe.typeSymbol
      require(ts.asClass.isCaseClass)

      val cMethodSym = constructor(tipe)
      var params = ivectorEmpty[Param]
      for (p <- cMethodSym.paramLists.head) {
        val name = p.name.decodedName.toString
        val anns =
          if (processAnnotations) {
            val fd = tipe.decl(TermName(name))
            fd.annotations.toVector.map(annotation(_, m))
          } else
            ivectorEmpty
        params :+= Param(name, p.typeSignature, anns, None)
      }
      val anns =
        if (processAnnotations) ts.annotations.toVector.map(annotation(_, m))
        else ivectorEmpty
      CaseClass(fullName(tipe), tipe, anns, params, imapEmpty[Any, Any])
    }

    def caseClassObject[T <: Product](
      t : T, processAnnotations : Boolean,
      seen : MIdMap[Any, Any] = idmapEmpty) : CaseClass = {

      val result = caseClassType(t.getClass, processAnnotations).copy()
      seen.put(t, result)

      implicit val iseen = seen
      implicit val ipa = processAnnotations

      var params = ivectorEmpty[Param]
      for (i <- 0 until result.params.size)
        params :+= result.params(i).copy(arg = Some(value(t.productElement(i))))
      result._params = params

      var properties = imapEmpty[Any, Any]
      t match {
        case t : PropertyProvider =>
          for ((k, v) <- t.propertyMap) properties += (value(k) -> value(v))
        case _ =>
      }
      result._properties = properties

      result
    }

    private def value(v : Any)(
      implicit processAnnotations : Boolean, seen : MIdMap[Any, Any]) : Any =
      if (seen.contains(v)) seen(v)
      else
        v match {
          case m : ILinkedMap[_, _] =>
            var newM = ilinkedMapEmpty[Any, Any]
            for ((k, v) <- m) newM += (value(k) -> value(v))
            newM
          case m : IMap[_, _] =>
            var newM = imapEmpty[Any, Any]
            for ((k, v) <- m) newM += (value(k) -> value(v))
            newM
          case tr : IVector[_] =>
            var newTr = ivectorEmpty[Any]
            for (e <- tr) newTr :+= value(e)
            newTr
          case tr : IList[_] =>
            var newTr = ilistEmpty[Any]
            for (e <- tr) newTr :+= value(e)
            newTr
          case p : Product => caseClassObject(p, processAnnotations, seen)
          case _           => v
        }

    /**
     * @author <a href="mailto:robby@k-state.edu">Robby</a>
     */
    case class Param(
      name : String,
      tipe : Type,
      annotations : ISeq[Annotation],
      arg : Option[Any])
  }
}
