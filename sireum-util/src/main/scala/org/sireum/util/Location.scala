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
trait Location

object Location {
  val locPropKey = "util.loc"
    
  trait XStreamer {
    self : org.sireum.util.XStreamer =>
    self.alias("loc.file", classOf[FileLocation.FileLocationWithAtImpl[_]])
    self.alias("loc.offset", classOf[OffsetLocation.OffsetLocationWithAtImpl[_]])
    self.alias("loc.linecolumn", classOf[LineColumnLocation.LineColumnLocationWithAtImpl[_]])
    self.alias("loc.linecolumn.end", classOf[BeginEndLineColumnLocation.BeginEndLineColumnLocationWithAtImpl[_]])
    self.alias("loc.file.linecolumn", classOf[FileLineColumnLocation.FileLineColumnLocationWithAtImpl[_]])
    self.alias("loc.source", classOf[SourceLocation.SourceLocationWithAtImpl[_]])
  }
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
trait FileLocation extends Location {
  var fileUri : FileResourceUri
  def fileUriOpt = if (fileUri == null) None else Some(fileUri)
  def fileUriForEach(f : FileResourceUri => Unit) {
    if (fileUri != null)
      f(fileUri)
  }
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object FileLocation {
  import language.implicitConversions
  implicit def pp2fl[T <: PropertyProvider](pp : T) : FileLocationWithAt[T] =
    pp.getPropertyOrElseUpdate(Location.locPropKey,
      new FileLocationWithAtImpl[T].context(pp))

  implicit object FileLocationPropertyAdapter
      extends Adapter[PropertyProvider, FileLocation] {
    def adapt(pp : PropertyProvider) : FileLocation = pp
  }

  trait FileLocationWithAt[T <: PropertyProvider]
      extends FileLocation {
    def at(fileUri : FileResourceUri) : T
  }

  private[util] final case class FileLocationWithAtImpl[T <: PropertyProvider](
    var fileUri : FileResourceUri = null)
      extends FileLocationWithAt[T] with PropertyProviderContext[T] {
    def at(fileUri : FileResourceUri) : T = {
      this.fileUri = fileUri
      context
    }
  }
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
trait OffsetLocation extends Location {
  var offset : Int
  var length : Int
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object OffsetLocation {
  import language.implicitConversions
  implicit def pp2ol[T <: PropertyProvider](pp : T) : OffsetLocationWithAt[T] =
    pp.getPropertyOrElseUpdate(Location.locPropKey,
      new OffsetLocationWithAtImpl[T].context(pp))

  implicit object OffsetLocationPropertyAdapter
      extends Adapter[PropertyProvider, OffsetLocation] {
    def adapt(pp : PropertyProvider) : OffsetLocation = pp
  }

  trait OffsetLocationWithAt[T <: PropertyProvider]
      extends OffsetLocation {
    def at(offset : Int, length : Int) : T
  }

  private[util] final case class OffsetLocationWithAtImpl[T <: PropertyProvider](
    var offset : Int = 0, var length : Int = 0)
      extends OffsetLocationWithAt[T] with PropertyProviderContext[T] {
    def at(offset : Int, length : Int) = {
      this.offset = offset
      this.length = length
      context
    }
  }
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
trait LineColumnLocation extends Location {
  var line : Int
  var column : Int
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object LineColumnLocation {
  import language.implicitConversions
  implicit def pp2lcl[T <: PropertyProvider](pp : T) : LineColumnLocationWithAt[T] =
    pp.getPropertyOrElseUpdate(Location.locPropKey,
      new LineColumnLocationWithAtImpl[T].context(pp))

  implicit object LineColumnLocationPropertyAdapter
      extends Adapter[PropertyProvider, LineColumnLocation] {
    def adapt(pp : PropertyProvider) : LineColumnLocation = pp
  }

  trait LineColumnLocationWithAt[T <: PropertyProvider]
      extends LineColumnLocation {
    def at(line : Int, column : Int) : T
  }

  private[util] final case class LineColumnLocationWithAtImpl[T <: PropertyProvider](
    var line : Int = 0, var column : Int = 0)
      extends LineColumnLocationWithAt[T] with PropertyProviderContext[T] {
    def at(line : Int, column : Int) = {
      this.line = line
      this.column = column
      context
    }
  }
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
trait BeginEndLineColumnLocation extends Location {
  var lineBegin : Int
  var columnBegin : Int
  var lineEnd : Int
  var columnEnd : Int
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object BeginEndLineColumnLocation {
  import language.implicitConversions
  implicit def pp2belcl[T <: PropertyProvider](
    pp : T) : BeginEndLineColumnLocationWithAt[T] =
    pp.getPropertyOrElseUpdate(Location.locPropKey,
      new BeginEndLineColumnLocationWithAtImpl[T].context(pp))

  implicit object BeginEndLineColumnLocationPropertyAdapter
      extends Adapter[PropertyProvider, BeginEndLineColumnLocation] {
    def adapt(pp : PropertyProvider) : BeginEndLineColumnLocation = pp
  }

  trait BeginEndLineColumnLocationWithAt[T <: PropertyProvider]
      extends BeginEndLineColumnLocation {
    def at(lineBegin : Int, columnBegin : Int, lineEnd : Int,
           columnEnd : Int) : T
  }

  private[util] final case class BeginEndLineColumnLocationWithAtImpl[T <: PropertyProvider](
    var lineBegin : Int = 0, var columnBegin : Int = 0,
    var lineEnd : Int = 0, var columnEnd : Int = 0)
      extends BeginEndLineColumnLocationWithAt[T] with PropertyProviderContext[T] {
    def at(lineBegin : Int, columnBegin : Int, lineEnd : Int,
           columnEnd : Int) = {
      this.lineBegin = lineBegin
      this.columnBegin = columnBegin
      this.lineEnd = lineEnd
      this.columnEnd = columnEnd
      context
    }
  }
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object FileLineColumnLocation {
  import language.implicitConversions
  implicit def pp2flcl[T <: PropertyProvider](pp : T) : FileLineColumnLocationWithAt[T] =
    pp.getPropertyOrElseUpdate(Location.locPropKey,
      new FileLineColumnLocationWithAtImpl[T].context(pp))

  implicit object FileLineColumnLocationPropertyAdapter
      extends Adapter[PropertyProvider, FileLocation with LineColumnLocation] {
    def adapt(pp : PropertyProvider) : FileLocation with LineColumnLocation =
      pp
  }

  trait FileLineColumnLocationWithAt[T <: PropertyProvider]
      extends FileLocation.FileLocationWithAt[T]
      with LineColumnLocation.LineColumnLocationWithAt[T] {

    def at(fileUri : Option[FileResourceUri], line : Int, column : Int) : T
    def at(fileUri : FileResourceUri, line : Int, column : Int) : T
    def at(line : Int, column : Int) : T
  }

  private[util] final case class FileLineColumnLocationWithAtImpl[T <: PropertyProvider](
    var fileUri : FileResourceUri = null, var line : Int = 0, var column : Int = 0)
      extends FileLineColumnLocationWithAt[T] with PropertyProviderContext[T] {
    def at(fileUri : Option[FileResourceUri], line : Int, column : Int) =
      if (fileUri.isDefined) at(fileUri.get, line, column)
      else at(line, column)

    def at(fileUri : FileResourceUri, line : Int, column : Int) = {
      this.fileUri = fileUri
      at(line, column)
    }
    def at(fileUri : FileResourceUri) : T = {
      this.fileUri = fileUri
      context
    }
    def at(line : Int, column : Int) = {
      this.line = line
      this.column = column
      context
    }
  }
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object SourceLocation {
  import language.implicitConversions
  implicit def pp2sl[T <: PropertyProvider](pp : T) : SourceLocationWithAt[T] =
    pp.getPropertyOrElseUpdate(Location.locPropKey,
      new SourceLocationWithAtImpl[T].context(pp))

  implicit object SourceLocationPropertyAdapter
      extends Adapter[PropertyProvider, FileLocation with BeginEndLineColumnLocation] {
    def adapt(pp : PropertyProvider) : FileLocation with BeginEndLineColumnLocation = pp
  }

  trait SourceLocationWithAt[T <: PropertyProvider]
      extends FileLocation.FileLocationWithAt[T]
      with BeginEndLineColumnLocation.BeginEndLineColumnLocationWithAt[T] {
    def at(fileUri : Option[FileResourceUri], lineBegin : Int, columnBegin : Int,
           lineEnd : Int, columnEnd : Int) : T
    def at(fileUri : FileResourceUri, lineBegin : Int, columnBegin : Int,
           lineEnd : Int, columnEnd : Int) : T
  }

  private[util] final case class SourceLocationWithAtImpl[T <: PropertyProvider](
    var fileUri : FileResourceUri = null, var lineBegin : Int = 0,
    var columnBegin : Int = 0, var lineEnd : Int = 0, var columnEnd : Int = 0)
      extends SourceLocationWithAt[T] with PropertyProviderContext[T] {
    def at(fileUri : Option[FileResourceUri], lineBegin : Int, columnBegin : Int,
           lineEnd : Int, columnEnd : Int) =
      if (fileUri.isDefined)
        at(fileUri, lineBegin, columnBegin, lineEnd, columnEnd)
      else
        at(lineBegin, columnBegin, lineEnd, columnEnd)
    def at(fileUri : FileResourceUri, lineBegin : Int, columnBegin : Int,
           lineEnd : Int, columnEnd : Int) = {
      this.fileUri = fileUri
      this.lineBegin = lineBegin
      this.columnBegin = columnBegin
      this.lineEnd = lineEnd
      this.columnEnd = columnEnd
      context
    }
    def at(fileUri : FileResourceUri) : T = {
      this.fileUri = fileUri
      context
    }
    def at(lineBegin : Int, columnBegin : Int, lineEnd : Int,
           columnEnd : Int) = {
      this.lineBegin = lineBegin
      this.columnBegin = columnBegin
      this.lineEnd = lineEnd
      this.columnEnd = columnEnd
      context
    }
  }
}
