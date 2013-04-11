/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object Tag {
  def lift[T <: Tag](f : T => Boolean, default : Boolean)(
    implicit m : Manifest[T]) : Tag => Boolean = {
    case t : T => f(t)
    case _     => default
  }

  def filter(tagType : TagType, f : Tag => Boolean, tags : Iterable[Tag]) = {
    val result = marrayEmpty[Tag]
    for (tag <- tags if tag.typ eq tagType)
      if (f(tag))
        result += tag
    result
  }

  def exists(tagType : TagType, f : Tag => Boolean, tags : Iterable[Tag]) = {
    var foundMatch = false
    for (tag <- tags if !foundMatch && (tag.typ eq tagType))
      if (f(tag))
        foundMatch = true
    foundMatch
  }

  def toTag(source : Option[FileResourceUri], tagLine : Int,
            tagColumn : Int, message : String,
            tagType : TagType) = {
    LocationTag(tagType, Some(message),
      if (source.isEmpty) {
        new LineColumnLocation {
          var line = tagLine
          var column = tagColumn
        }
      } else {
        new FileLocation with LineColumnLocation {
          var line = tagLine
          var column = tagColumn
          var fileUri = source.get
        }
      }
    )
  }

  def collate(tags : Iterable[Tag]) = {
    val result = mmapEmpty[Option[FileResourceUri], MArray[Tag]]
    for (t <- tags)
      t match {
        case lt @ LocationTag(t, desc, l : FileLocation) =>
          result.getOrElseUpdate(Some(l.fileUri), marrayEmpty[Tag]) += lt
        case lt @ LocationTag(t, desc, l) =>
          result.getOrElseUpdate(None, marrayEmpty[Tag]) += lt
        case it @ InfoTag(t, desc) =>
          result.getOrElseUpdate(None, marrayEmpty[Tag]) += it
      }
    result
  }

  def collateAsString(tags : Iterable[Tag]) = {
    val m = collate(tags)
    val sb = new StringBuilder
    for ((f, ts) <- m) {
        def tos =
          for (t <- ts)
            t match {
              case lt @ LocationTag(_, msg, t) =>
                val text = msg.getOrElse("?")
                t match {
                  case t : LineColumnLocation =>
                    sb.append(s"  - [${t.line}, ${t.column}] $text\n")
                  case t : OffsetLocation =>
                    sb.append(s"  - @[${t.offset}, ${t.length}] $text\n")
                  case t : FileLocation =>
                    sb.append(s"  - $text\n")
                }
              case it @ InfoTag(t, msg) =>
                val text = msg.getOrElse("?")
                sb.append(s"  - $text")
            }
      f match {
        case Some(fileUri) =>
          sb.append("* On file: %s\n".format(fileUri))
        case _ =>
          sb.append("* Error(s)/Warning(s):\n")
      }
      tos
    }
    sb.toString
  }
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
trait TagResource {
  protected var _uriScheme : String = null
  protected var _uriType : String = null
  protected var _uriPaths : ISeq[String] = null
  protected var _uri : ResourceUri = null
  protected var _set : Boolean = false

  def uriScheme = { require(_set); _uriScheme }

  def uriType = { require(_set); _uriType }

  def uriPaths = { require(_set); _uriPaths }

  def uri = { require(_set); _uri }

  def hasResourceInfo = _set

  def uri(scheme : String, typ : String,
          paths : ISeq[String], uri : ResourceUri) {
    require(paths != null && paths.forall(_ != null) && uri != null && !_set)
    _set = true
    this._uriScheme = scheme
    this._uriType = typ
    this._uriPaths = paths
    this._uri = uri
  }
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
sealed abstract class TagType extends ResourceDefinition with TagResource {
  def name : String
  def title : String
  def description : Option[String]
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
case class HighlightTagType(
  name : String,
  description : Option[String] = None,
  title : String,
  red : Byte,
  green : Byte,
  blue : Byte,
  layer : Int = 0)
    extends TagType

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
case class ImageTagType(
  name : String,
  description : Option[String] = None,
  title : String,
  imageLocation : FileLocation)
    extends TagType

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object MarkerTagKind extends Enum {
  sealed abstract class Type extends EnumElem
  object Problem extends Type
  object Task extends Type
  object Bookmark extends Type
  object Text extends Type

  def elements = ivector(Problem, Task, Bookmark, Text)
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object MarkerTagSeverity extends Enum {
  sealed abstract class Type extends EnumElem
  object Info extends Type
  object Warning extends Type
  object Error extends Type

  def elements = ivector(Info, Warning, Error)
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object MarkerTagPriority extends Enum {
  sealed abstract class Type extends EnumElem
  object Low extends Type
  object Normal extends Type
  object High extends Type

  def elements = ivector(Low, Normal, High)
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
case class MarkerType(
  name : String,
  description : Option[String] = None,
  title : String,
  severity : MarkerTagSeverity.Type,
  priority : MarkerTagPriority.Type,
  kinds : ISeq[MarkerTagKind.Type])
    extends TagType

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
class TagConfiguration(name : String, groups : ISeq[TagGroup])
  extends ResourceDefinition with TagResource

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
class TagGroup(
  name : String,
  title : String,
  description : Option[String] = None)
    extends ResourceDefinition with TagResource

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
sealed abstract class Tag extends PropertyProvider {
  lazy val propertyMap = mmapEmpty[Property.Key, Any]

  def typ : TagType
  def description : Option[String]
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
case class InfoTag(typ : TagType, description : Option[String] = None)
  extends Tag

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
case class LocationTag(
  typ : TagType,
  description : Option[String] = None,
  location : Location)
    extends Tag
