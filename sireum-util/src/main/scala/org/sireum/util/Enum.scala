/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util
import scala.collection.mutable.LinkedHashSet

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
trait Enum {
  context =>

  def elements : IVector[EnumElem]

  private def init {
    if (!initialized) {
      initialized = true
      assert(elements.size == elems.size)
      for (e <- elements)
        assert(elems(e) >= 0)
    }
  }

  private var initialized = false

  private val elems : MLinkedMap[EnumElem, Int] = mlinkedMapEmpty

  def valueOf(elemName : String) : Option[EnumElem] = {
    init
    for (e <- elements) {
      if (e.toString == elemName)
        return Some(e)
    }
    None
  }

  abstract class EnumElem {
    elems += (this -> elems.size)

    def enum : Enum = context

    def elements : Iterable[EnumElem] = {
      init
      context.elems.keys
    }

    def ordinal : Int = {
      init
      context.elems(this)
    }

    def maxOrdinal = {
      init
      context.elems.size - 1
    }

    override def toString = {
      init
      val s = getClass.getSimpleName()
      val i = s.lastIndexOf('$', s.length - 2)
      if (i >= 0) s.substring(i + 1, s.length - 1)
      else s
    }
  }
}

