/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

import java.io._
import java.net.URI
import java.nio.file._
import java.nio.file.attribute._

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object FileUtil {

  def toFile(fileUri: FileResourceUri) =
    new File(new URI(fileUri))

  def fileUri(claz: Class[_], path: String) =
    toUri(new File(claz.getResource(path).toURI))

  def toUri(path: String): FileResourceUri = toUri(new File(path))

  def toUri(f: File): FileResourceUri = f.getCanonicalFile.toURI.toASCIIString

  def toFilePath(fileUri: FileResourceUri) =
    toFile(fileUri).getAbsolutePath

  def filename(fileUri: FileResourceUri) =
    toFile(fileUri).getName

  def listFiles(dirUri: FileResourceUri, ext: String,
                recursive: Boolean = false,
                result: MArray[FileResourceUri] = marrayEmpty[FileResourceUri]) //
  : ISeq[FileResourceUri] = {
    val dir = toFile(dirUri)
    if (dir.exists)
      dir.listFiles(new FilenameFilter {
        def accept(dir: File, name: String) = name.endsWith(ext)
      }).foreach { f => if (f.isFile) result += toUri(f) }
    if (recursive)
      dir.listFiles.foreach { f =>
        if (f.isDirectory) listFiles(toUri(f), ext, recursive, result)
      }
    result.toList
  }

  def readFile(r: java.io.Reader): String = {
    val buffer = new Array[Char](1024)
    var n = r.read(buffer)
    val sb = new StringBuilder
    while (n != -1) {
      sb.appendAll(buffer, 0, n)
      n = r.read(buffer)
    }
    sb.toString
  }

  def readFile(fileUri: FileResourceUri): (String, FileResourceUri) = {
    val uri = new URI(fileUri)
    val file = new File(uri)

    assert(file.exists)

    val size = file.length

    assert(size < Int.MaxValue)

    val buffer = new Array[Byte](size.toInt)
    val stream = uri.toURL.openStream
    stream.read(buffer)
    (new String(buffer), file.getAbsoluteFile.toURI.toASCIIString)
  }

  def readFileLines(fileUri: FileResourceUri): (ISeq[String], FileResourceUri) = {
    val uri = new URI(fileUri)
    val file = new File(uri)

    assert(file.exists)

    val lineSep = System.lineSeparator
    val stream = uri.toURL.openStream
    val lnr = new LineNumberReader(new InputStreamReader(stream))
    var line = lnr.readLine
    var result = ivectorEmpty[String]
    while (line != null) {
      result :+= line
      line = lnr.readLine
    }
    (result, file.getAbsoluteFile.toURI.toASCIIString)
  }

  def walkFileTree(d: Path, f: (Boolean, Path) => Unit, isDir: Boolean) {
    Files.walkFileTree(d, new SimpleFileVisitor[Path] {
      override def preVisitDirectory(
                                      d: Path, attrs: BasicFileAttributes) = {
        f(true, d)
        FileVisitResult.CONTINUE
      }

      override def visitFile(
                              p: Path, attrs: BasicFileAttributes) = {
        if (!isDir) f(false, p)
        FileVisitResult.CONTINUE
      }
    })
  }

  def delete(d: Path): Boolean = {
    var ok = true
    Files.walkFileTree(d, new SimpleFileVisitor[Path] {
      override def postVisitDirectory(d: Path,
                                      exc: IOException) =
        delete(d)

      override def visitFile(p: Path,
                             attrs: BasicFileAttributes) =
        delete(p)

      override def visitFileFailed(p: Path, exc: IOException) =
        delete(p)

      private def delete(p: Path) = {
        ok = ok && Files.deleteIfExists(p)
        if (ok) FileVisitResult.CONTINUE
        else FileVisitResult.TERMINATE
      }
    })
    ok
  }

  def writeFile(fileUri: FileResourceUri, content: String): Unit = {
    val fw = new FileWriter(toFile(fileUri))
    try fw.write(content) finally fw.close()
  }
}