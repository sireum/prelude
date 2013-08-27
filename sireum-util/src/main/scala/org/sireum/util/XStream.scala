/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

import java.io._

import com.thoughtworks.xstream._
import com.thoughtworks.xstream.converters._
import com.thoughtworks.xstream.mapper._
import com.thoughtworks.xstream.io._
import com.thoughtworks.xstream.io.json._
import com.thoughtworks.xstream.io.xml._

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object XStreamer {

  /**
   * @author <a href="mailto:robby@k-state.edu">Robby</a>
   */
  object Mode extends Enumeration {
    type Type = Value
    val XML, JSON, BI_JSON = Value
  }

  private final val nullName = "null"
  private final val stringLengthThreshold = 16

  private val toValue : // 
  scala.reflect.runtime.universe.Type --> (String => Any) = {
    import org.sireum.util.math._
    import Reflection._
    import java.lang._
    {
      case t if t =:= booleanType => Boolean.valueOf _
      case t if t =:= charType    => _(0)
      case t if t =:= shortType   => Short.valueOf _
      case t if t =:= intType     => Integer.valueOf _
      case t if t =:= longType    => Long.valueOf _
      case t if t =:= floatType   => Float.valueOf _
      case t if t =:= doubleType  => Double.valueOf _
      case t if t =:= bigIntType  => BigInt.apply _
      case t if t =:= integerType => { s => SireumNumber(BigInt(s)) }
      case t if t =:= stringType  => identity
    }
  }

  def apply(
    isDietModeSkipProperties : (Boolean, XStreamer.Mode.Type, Boolean) = // 
    (true, Mode.XML, false)) = new XStreamer {
    override val (isDiet, mode, skipProperties) = isDietModeSkipProperties
  }
  /**
   * @author <a href="mailto:robby@k-state.edu">Robby</a>
   */
  trait ConverterContext {
    final val propertyProviderNodeName = "properties"

    def isXml : Boolean
    def skipProperties : Boolean
    def mapper : Mapper
    def traversableConverter : TraversableConverter
    def mapConverter : MapConverter

    def getName(c : Class[_]) : String = {
      var name = mapper.serializedClass(c)
      if (isXml) {
        name = name.replace("$", "_-")
      }
      name
    }

    def getMappedClass(name : String) : Class[_] =
      mapper.realClass(if (isXml) name.replace("_-", "$") else name)

    def marshal(v : Any)(implicit writer : HierarchicalStreamWriter,
                         context : MarshallingContext) {
      val value = v.asInstanceOf[Object]
      if (value == null)
        node(nullName) {}
      else {
        val valueClass = value.getClass
        if (traversableConverter.canConvert(valueClass))
          node(TraversableConverter.mconverter(value)) {
            traversableConverter.marshal(value, writer, context)
          }
        else if (mapConverter.canConvert(valueClass))
          node(MapConverter.mconverter(value)) {
            mapConverter.marshal(value, writer, context)
          }
        else
          node(getName(valueClass)) {
            context.convertAnother(value)
          }
      }
    }

    def unmarshal(name : String)(
      implicit reader : HierarchicalStreamReader,
      context : UnmarshallingContext) : Object = {
      if (name == nullName)
        null
      else if (TraversableConverter.uconverter.isDefinedAt(name))
        traversableConverter.unmarshal(reader, context)
      else if (MapConverter.uconverter.isDefinedAt(name))
        mapConverter.unmarshal(reader, context)
      else {
        val elementClass = getMappedClass(name)
        context.convertAnother(null, elementClass)
      }
    }

    def marshalPropertyProvider(o : Object)(
      implicit writer : HierarchicalStreamWriter,
      context : MarshallingContext) {
      o match {
        case pp : PropertyProvider if !skipProperties && !pp.propertyEmpty =>
          node(propertyProviderNodeName) {
            context.convertAnother(pp.propertyMap)
          }
        case _ =>
      }
    }

    def unmarshalPropertyProvider(o : Object)(
      implicit reader : HierarchicalStreamReader,
      context : UnmarshallingContext) {
      o match {
        case pp : PropertyProviderInit if reader.hasMoreChildren =>
          unnode(propertyProviderNodeName) {
            val m = context.convertAnother(o, classOf[PropertyProvider.Map]).
              asInstanceOf[PropertyProvider.Map]
            if (!m.isEmpty)
              pp.propertyMap = m
          }
        case pp : PropertyProvider if reader.hasMoreChildren =>
          unnode(propertyProviderNodeName) {
            val m = context.convertAnother(o, classOf[PropertyProvider.Map]).
              asInstanceOf[PropertyProvider.Map]
            if (!m.isEmpty)
              pp.propertyMap ++= m
          }
        case _ =>
      }
    }
  }

  /**
   * @author <a href="mailto:robby@k-state.edu">Robby</a>
   */
  trait ConverterContextInit extends ConverterContext {
    def traversableConverter_=(tc : TraversableConverter)
    def mapConverter_=(tc : MapConverter)
  }

  /**
   * @author <a href="mailto:robby@k-state.edu">Robby</a>
   */
  class OptionConverter(ctx : ConverterContext) extends Converter {
    final val classAttributeName = "etype"
    final val someNodeName = "some"

    def canConvert(clazz : Class[_]) =
      classOf[Option[_]].isAssignableFrom(clazz)

    def marshal(value : Object, writer : HierarchicalStreamWriter,
                context : MarshallingContext) {
      value match {
        case Some(o) => 
          implicit val iw = writer
          writer.addAttribute(classAttributeName,
            ctx.getName(o.getClass))
          context.convertAnother(o)
        case _ =>
      }
    }

    def unmarshal(reader : HierarchicalStreamReader,
                  context : UnmarshallingContext) : Object = {
      val am = attributes(reader)
      am.get(classAttributeName) match {
        case Some(n) => Some(context.convertAnother(null, ctx.getMappedClass(n)))
        case _       => None
      }
    }
  }

  /**
   * @author <a href="mailto:robby@k-state.edu">Robby</a>
   */
  class StringConverter extends Converter {

    final val stringAttributeName = "sid"
    final val stringRefAttributeName = "srefid"

    val seen = mmapEmpty[Object, Int]
    val seenInverse = mmapEmpty[Int, String]

    def canConvert(clazz : Class[_]) =
      classOf[java.lang.String].isAssignableFrom(clazz)

    def marshal(value : Object, writer : HierarchicalStreamWriter,
                context : MarshallingContext) {
      val s = value.toString
      if (s.length < stringLengthThreshold)
        writer.setValue(s)
      else seen.get(value) match {
        case Some(id) =>
          writer.addAttribute(stringRefAttributeName, id.toString)
        case _ =>
          val id = seen.size + 1
          seen(value) = id
          writer.addAttribute(stringAttributeName, id.toString)
          writer.setValue(value.toString)
      }
    }

    def unmarshal(reader : HierarchicalStreamReader,
                  context : UnmarshallingContext) : Object = {
      val am = attributes(reader)
      if (am.get(nullName).isDefined) null
      else if (am.get(stringAttributeName).isEmpty &&
        am.get(stringRefAttributeName).isEmpty) reader.getValue
      else am.get(stringRefAttributeName) match {
        case Some(id) => seenInverse(id.toInt)
        case _ =>
          val id = am(stringAttributeName).toInt
          val s = reader.getValue
          seenInverse(id) = s
          s
      }
    }
  }

  /**
   * @author <a href="mailto:robby@k-state.edu">Robby</a>
   */
  class CaseClassConverter(ctx : ConverterContext) extends Converter {
    final val caseClassAttributeName = "cid"
    final val caseClassRefAttributeName = "crefid"
    final val classAttributeName = "type"
    final val entryNodeName = "entry"

    val seen = idmapEmpty[Object, Int]
    val seenInverse = mmapEmpty[Int, Object]

    def canConvert(clazz : Class[_]) =
      classOf[Product].isAssignableFrom(clazz) &&
        !clazz.getName.startsWith("scala.")

    def marshal(value : Object, writer : HierarchicalStreamWriter,
                context : MarshallingContext) {
      implicit val iw = writer
      seen.get(value) match {
        case Some(id) =>
          writer.addAttribute(caseClassRefAttributeName, id.toString)
        case _ =>
          val id = seen.size + 1
          seen(value) = id

          writer.addAttribute(caseClassAttributeName, id.toString)

          val clazz = value.getClass
          val cc = Reflection.CaseClass.caseClassType(value.getClass, false)
          val params = cc.params
          val p = value.asInstanceOf[Product]

          val elementParams = {
            if (ctx.isXml) {
              var elementParams = ivectorEmpty[Int]
              for (i <- 0 until params.size) {
                val name = params(i).name
                val value = p.productElement(i)
                getStringIfNative(params(i).tipe, value) match {
                  case Some(value) => writer.addAttribute(name, value)
                  case _           => elementParams :+= i
                }
              }
              elementParams
            } else
              0 until params.size
          }
          val tc = ctx.traversableConverter
          val mc = ctx.mapConverter

          for (i <- elementParams) {
            val name = params(i).name
            val value = p.productElement(i).asInstanceOf[Object]
            node(name) {
              if (value != null) {
                val valueClass = value.getClass
                if (tc.canConvert(valueClass)) {
                  tc.marshal(value, writer, context)
                } else if (mc.canConvert(valueClass)) {
                  mc.marshal(value, writer, context)
                } else {
                  writer.addAttribute(classAttributeName, ctx.getName(valueClass))
                  context.convertAnother(value)
                }
              } else writer.addAttribute(nullName, "true")
            }
          }

          ctx.marshalPropertyProvider(value)(writer, context)
      }
    }

    def unmarshal(reader : HierarchicalStreamReader,
                  context : UnmarshallingContext) : Object = {
      val am = attributes(reader)
      if (am.get(nullName).isDefined) null
      else {
        implicit val ir = reader
        am.get(caseClassRefAttributeName) match {
          case Some(id) =>
            seenInverse(id.toInt)
          case _ =>
            val clazz = context.getRequiredType
            val cc = Reflection.CaseClass.caseClassType(clazz, false)
            val params = cc.params
            var args = ivectorEmpty[Object]
            val am = attributes(reader)
            for (i <- 0 until params.size) {
              val param = params(i)
              val name = param.name
              val tipe = param.tipe
              am.get(name) match {
                case Some(value) =>
                  args :+= toValue(tipe)(value).asInstanceOf[Object]
                case None =>
                  unnode(name) {
                    val tc = ctx.traversableConverter
                    val mc = ctx.mapConverter
                    val am2 = attributes(reader)
                    if (am2.contains(nullName))
                      args :+= null
                    else {
                      if (am2.contains(TraversableConverter.typeAttributeName))
                        args :+= tc.unmarshal(reader, context)
                      else if (am2.contains(MapConverter.typeAttributeName))
                        args :+= mc.unmarshal(reader, context)
                      else {
                        val argClass = ctx.getMappedClass(am2(classAttributeName))
                        args :+= context.convertAnother(null, argClass)
                      }
                    }
                  }
              }
            }

            val result = ProductUtil.make(clazz, args : _*).asInstanceOf[Object]
            val id = am(caseClassAttributeName).toInt
            seenInverse(id) = result
            ctx.unmarshalPropertyProvider(result)(reader, context)
            result
        }
      }
    }
  }

  /**
   * @author <a href="mailto:robby@k-state.edu">Robby</a>
   */
  class ProductConverter(ctx : ConverterContext)
      extends Converter {
    final val caseClassAttributeName = "cid"
    final val caseClassRefAttributeName = "crefid"
    final val classAttributeName = "type"
    final val sizeAttributeName = "size"
    final val propertyProviderNodeName = "properties"
    final val entryNodeName = "entry"

    val seen = idmapEmpty[Object, Int]
    val seenInverse = mmapEmpty[Int, Object]

    def canConvert(clazz : Class[_]) =
      classOf[Product].isAssignableFrom(clazz) &&
        !clazz.getName.startsWith("scala.")

    def marshal(value : Object, writer : HierarchicalStreamWriter,
                context : MarshallingContext) {
      seen.get(value) match {
        case Some(id) =>
          writer.addAttribute(caseClassRefAttributeName, id.toString)
        case _ =>
          val id = seen.size + 1
          seen(value) = id

          writer.addAttribute(caseClassAttributeName, id.toString)

          val p = value.asInstanceOf[Product]

          writer.addAttribute(sizeAttributeName, p.productArity.toString)

          implicit val iw = writer
          implicit val ic = context
          for (i <- 0 until p.productArity)
            ctx.marshal(p.productElement(i).asInstanceOf[Object])

          ctx.marshalPropertyProvider(value)(writer, context)
      }
    }

    def unmarshal(reader : HierarchicalStreamReader,
                  context : UnmarshallingContext) : Object = {
      val am = attributes(reader)
      if (am.get(nullName).isDefined) null
      else {
        am.get(caseClassRefAttributeName) match {
          case Some(id) =>
            seenInverse(id.toInt)
          case _ =>
            val clazz = context.getRequiredType
            val size = am(sizeAttributeName).toInt
            val elements = marrayEmpty[Object]

            implicit val ir = reader
            implicit val ic = context
            for (i <- 0 until size)
              elements += unnodef(ctx.unmarshal(_))

            val result = ProductUtil.make(clazz, elements : _*).asInstanceOf[Object]
            val id = am(caseClassAttributeName).toInt
            seenInverse(id) = result
            ctx.unmarshalPropertyProvider(result)(reader, context)
            result
        }
      }
    }
  }

  /**
   * @author <a href="mailto:robby@k-state.edu">Robby</a>
   */
  object TraversableConverter {
    final val typeAttributeName = "ttype"
    final val ilistType = "ilist"
    final val isetType = "iset"      
    final val ivectorType = "ivector"
    final val marrayType = "marray"
      
    val mconverter : Any --> String = {
      case t : IList[_]   => ilistType
      case t : ISet[_]    => isetType
      case t : IVector[_] => ivectorType
      case t : MArray[_]  => marrayType
    }

    val uconverter : String --> Traversable[Any] = {
      case `ilistType`   => ilistEmpty[Any]
      case `isetType`    => isetEmpty[Any]
      case `ivectorType` => ivectorEmpty[Any]
      case `marrayType`  => marrayEmpty[Any]
    }
  }

  /**
   * @author <a href="mailto:robby@k-state.edu">Robby</a>
   */
  class TraversableConverter(ctx : ConverterContextInit) extends Converter {
    import TraversableConverter._

    ctx.traversableConverter = this

    def canConvert(clazz : Class[_]) = {
      classOf[IList[_]].isAssignableFrom(clazz) ||
      classOf[ISet[_]].isAssignableFrom(clazz) ||
      classOf[IVector[_]].isAssignableFrom(clazz) ||
      classOf[MArray[_]].isAssignableFrom(clazz)
    }

    def marshal(value : Object, writer : HierarchicalStreamWriter,
                context : MarshallingContext) {
      writer.addAttribute(typeAttributeName, mconverter(value))
      implicit val iw = writer
      implicit val ic = context
      val t = value.asInstanceOf[Traversable[Any]]
      for (e <- t)
        ctx.marshal(e)
    }

    def unmarshal(reader : HierarchicalStreamReader,
                  context : UnmarshallingContext) : Object = {
      val am = attributes(reader)
      if (am.get(nullName).isDefined) null
      else {
        implicit val ir = reader
        implicit val ic = context
        uconverter(am(typeAttributeName)) match {
          case t : ISeq[Any] =>
            var t1 = t.asInstanceOf[ISeq[Any]]
            while (reader.hasMoreChildren)
              t1 :+= unnodef(ctx.unmarshal(_))
            t1
          case t : ISet[Any] =>
            var t1 = t.asInstanceOf[ISet[Any]]
            while (reader.hasMoreChildren)
              t1 += unnodef(ctx.unmarshal(_))
            t1
          case t : MBuffer[Any] =>
            while (reader.hasMoreChildren)
              t += unnodef(ctx.unmarshal(_))
            t
        }
      }
    }
  }

  /**
   * @author <a href="mailto:robby@k-state.edu">Robby</a>
   */
  object MapConverter {
    final val typeAttributeName = "mtype"
    final val imapType = "imap"
    final val ilinkedMapType = "ilinkedmap"
    final val mmapType = "mmap"
    final val mlinkedMapType = "mlinkedmap"

    val mconverter : Any --> String = {
      case m : ILinkedMap[_, _] => ilinkedMapType
      case m : IMap[_, _]       => imapType
      case m : MLinkedMap[_, _] => mlinkedMapType
      case m : MMap[_, _]       => mmapType
    }

    val uconverter : String --> CMap[Any, Any] = {
      case `ilinkedMapType` => ilinkedMapEmpty[Any, Any]
      case `imapType`       => imapEmpty[Any, Any]
      case `mlinkedMapType` => mlinkedMapEmpty[Any, Any]
      case `mmapType`       => mmapEmpty[Any, Any]
    }
  }

  /**
   * @author <a href="mailto:robby@k-state.edu">Robby</a>
   */
  class MapConverter(ctx : ConverterContextInit) extends Converter {
    import MapConverter._
    final val entryNodeName = "entry"

    ctx.mapConverter = this

    def canConvert(clazz : Class[_]) =
      classOf[scala.collection.Map[_, _]].isAssignableFrom(clazz)

    def marshal(value : Object, writer : HierarchicalStreamWriter,
                context : MarshallingContext) {
      writer.addAttribute(typeAttributeName, mconverter(value))

      implicit val iw = writer
      implicit val ic = context
      for ((k, v) <- value.asInstanceOf[CMap[Any, Any]])
        node(entryNodeName) {
          ctx.marshal(k)
          ctx.marshal(v)
        }
    }

    def unmarshal(reader : HierarchicalStreamReader,
                  context : UnmarshallingContext) : Object = {
      val am = attributes(reader)
      if (am.get(nullName).isDefined) null
      else {
        implicit val ir = reader
        implicit val ic = context
        var r = uconverter(am(typeAttributeName))
        r match {
          case m : IMap[Any, Any] =>
            while (reader.hasMoreChildren)
              unnode(entryNodeName) {
                r += (unnodef(ctx.unmarshal(_)) -> unnodef(ctx.unmarshal(_)))
              }
            r
          case m : MMap[Any, Any] =>
            while (reader.hasMoreChildren)
              unnode(entryNodeName) {
                m(unnodef(ctx.unmarshal(_))) = unnodef(ctx.unmarshal(_))
              }
            m
        }
      }
    }
  }

  @inline
  private def node(name : String)(f : => Unit)(
    implicit writer : HierarchicalStreamWriter) {
    writer.startNode(name)
    f
    writer.endNode
  }

  @inline
  private def unnode[T](name : String)(f : => T)(
    implicit reader : HierarchicalStreamReader) : T = {
    reader.moveDown
    assert(reader.getNodeName == name)
    val result = f
    reader.moveUp
    result
  }

  @inline
  private def unnodef[T](f : String => T)(
    implicit reader : HierarchicalStreamReader) : T = {
    reader.moveDown
    val result = f(reader.getNodeName)
    reader.moveUp
    result
  }

  private def attributes(r : HierarchicalStreamReader) : IMap[String, String] = {
    import scala.collection.JavaConversions._
    var result = imapEmpty[String, String]
    for (name <- r.getAttributeNames.asInstanceOf[java.util.Iterator[String]]) {
      result += (name -> r.getAttribute(name))
    }
    result
  }

  private def getStringIfNative(
    t : scala.reflect.runtime.universe.Type, o : Any) : Option[String] = {
    if (toValue.isDefinedAt(t))
      o match {
        case v : Boolean                      => Some(v.toString)
        case v : Character                    => Some(v.toString)
        case v : Short                        => Some(v.toString)
        case v : Int                          => Some(v.toString)
        case v : Long                         => Some(v.toString)
        case v : Float                        => Some(v.toString)
        case v : Double                       => Some(v.toString)
        case v : BigInt                       => Some(v.toString)
        case v : org.sireum.util.math.Integer => Some(v.toBigInt.toString)
        case v : String if v.length < stringLengthThreshold =>
          Some(v)
        case _ => None
      }
    else None
  }
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
trait XStreamer {
  self =>

  import XStreamer._

  def isDiet : Boolean = true
  def skipProperties : Boolean = false
  def mode : Mode.Type = Mode.XML

  private val _converters = marrayEmpty[Converter]
  private val _sconverters = marrayEmpty[SingleValueConverter]
  private val _packageAliases = marrayEmpty[(String, String)]
  private val _classAliases = marrayEmpty[(String, Class[_])]

  {
    alias("some", classOf[Some[_]])
    alias("none", None.getClass)
    alias("single", classOf[Tuple1[_]])
    alias("pair", classOf[Tuple2[_, _]])
    alias("triple", classOf[Tuple3[_, _, _]])
    alias("quadruple", classOf[Tuple4[_, _, _, _]])
    alias("pentuple", classOf[Tuple5[_, _, _, _, _]])
    alias("hextuple", classOf[Tuple6[_, _, _, _, _, _]])
    alias("septuple", classOf[Tuple7[_, _, _, _, _, _, _]])
    alias("octuple", classOf[Tuple8[_, _, _, _, _, _, _, _]])
    alias("nonuple", classOf[Tuple9[_, _, _, _, _, _, _, _, _]])
    alias("decuple", classOf[Tuple10[_, _, _, _, _, _, _, _, _, _]])
    alias("tuple11", classOf[Tuple11[_, _, _, _, _, _, _, _, _, _, _]])
    alias("tuple12", classOf[Tuple12[_, _, _, _, _, _, _, _, _, _, _, _]])
    alias("tuple13", classOf[Tuple13[_, _, _, _, _, _, _, _, _, _, _, _, _]])
    alias("tuple14", classOf[Tuple14[_, _, _, _, _, _, _, _, _, _, _, _, _, _]])
    alias("tuple15", classOf[Tuple15[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _]])
    alias("tuple16", classOf[Tuple16[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]])
    alias("tuple17", classOf[Tuple17[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]])
    alias("tuple18", classOf[Tuple18[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]])
    alias("tuple19", classOf[Tuple19[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]])
    alias("tuple20", classOf[Tuple20[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]])
    alias("tuple21", classOf[Tuple21[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]])
    alias("tuple22", classOf[Tuple22[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]])
    aliasPackage("icol", "scala.collection.immutable")
    aliasPackage("mcol", "scala.collection.mutable")
  }

  def converters : Seq[Converter] = _converters
  def singleValueConverters : Seq[SingleValueConverter] = _sconverters
  def packageAliases : Seq[(String, String)] = _packageAliases
  def classAliases : Seq[(String, Class[_])] = _classAliases

  def xstream = {
    val xstream = new XStream(
      mode match {
        case Mode.XML     => new Xpp3Driver
        case Mode.JSON    => new JsonHierarchicalStreamDriver
        case Mode.BI_JSON => new JettisonMappedXmlDriver
      })
    xstream.setMode(XStream.NO_REFERENCES)

    for ((a, p) <- packageAliases) xstream.aliasPackage(a, p)

    for ((a, c) <- classAliases) xstream.aliasType(a, c)
    
    val ctx = new ConverterContextInit {
      val isXml = mode == Mode.XML
      val isDiet = self.isDiet
      val skipProperties = self.skipProperties
      val mapper = xstream.getMapper
      var traversableConverter : TraversableConverter = _
      var mapConverter : MapConverter = _
    }

    xstream.registerConverter(new TraversableConverter(ctx))

    xstream.registerConverter(new MapConverter(ctx))

    xstream.registerConverter(
      if (isDiet) new ProductConverter(ctx)
      else new CaseClassConverter(ctx))

    xstream.registerConverter(new StringConverter)

    xstream.registerConverter(new OptionConverter(ctx))
    
    for (c <- converters) xstream.registerConverter(c)

    for (c <- singleValueConverters) xstream.registerConverter(c)

    xstream
  }

  def to(o : Any) = xstream.toXML(o)

  def to(o : Any, os : OutputStream) = xstream.toXML(o, os)

  def to(o : Any, w : Writer) = xstream.toXML(o, w)

  def from(o : String) = xstream.fromXML(o)

  def from(is : InputStream) = xstream.fromXML(is)

  def from(r : Reader) = xstream.fromXML(r)

  def registerConverters(cs : Converter*) {
    _converters ++= cs
  }

  def registerSingleConverters(cs : SingleValueConverter*) {
    _sconverters ++= cs
  }

  def aliasPackage(alias : String, packageName : String) {
    _packageAliases += ((alias, packageName))
  }

  def alias(alias : String, clazz : Class[_]) {
    _classAliases += ((alias, clazz))
  }
}
