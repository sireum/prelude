/*
Copyright (c) 2014 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

import java.io._

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object Database {
  def apply() : Database = new DatabaseImpl(None)
  def apply(file : File) : Database = new DatabaseImpl(Some(file))
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
trait Database extends Closeable {
  def dbKeys : CSet[String]
  def delete(dbKey : String)
  def exists(dbKey : String) : Boolean
  def keys(dbKey : String) : CSet[Any]
  def load[V <: Any](dbKey : String, key : Any) : Option[V]
  def remove[V <: Any](dbKey : String, key : Any) : Option[V]
  def store[V <: Any](dbKey : String, key : Any, value : V) : Option[V]
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
private final class DatabaseImpl(dbFile : Option[File]) extends Database {
  private final val OPS_THRESHOLD = 5

  import org.mapdb._

  import language.implicitConversions

  implicit def v2vopt[V](v : V) = if (v != null) Some(v) else None

  private val db =
    dbFile match {
      case Some(file) =>
        DBMaker.newFileDB(file).
          mmapFileEnable.closeOnJvmShutdown.make
      case _ =>
        DBMaker.newTempFileDB.mmapFileEnable.make
    }

  private var isLastOpStoring = false

  private var numOfOps = 0

  def close {
    db.close
  }

  def dbKeys : CSet[String] = {
    import scala.collection.JavaConversions._
    db.getAll.keySet
  }

  def delete(dbKey : String) {
    db.delete(dbKey)
    db.compact
    numOfOps = 0
    isLastOpStoring = false
  }

  def exists(dbKey : String) : Boolean = db.exists(dbKey)
  
  def keys(dbKey : String) : CSet[Any] = {
    import scala.collection.JavaConversions._
    db.getHashMap[Any, Any](dbKey).keySet
  }

  def load[V <: Any](dbKey : String, key : Any) : Option[V] =
    db.getHashMap[Any, V](dbKey).get(key)

  def remove[V <: Any](dbKey : String, key : Any) : Option[V] = {
    val r = db.getHashMap[Any, V](dbKey).remove(key)
    db.commit
    if (isLastOpStoring) {
      isLastOpStoring = false
      compact
    }
    r
  }

  def store[V <: Any](dbKey : String, key : Any, value : V) : Option[V] = {
    val r = db.getHashMap[Any, V](dbKey).put(key, value)
    db.commit
    if (!isLastOpStoring) {
      isLastOpStoring = true
      compact
    }
    r
  }
  
  private def compact {
    numOfOps += 1
    if (numOfOps > OPS_THRESHOLD) {
      numOfOps = 0
      db.compact
    }
  }

} 