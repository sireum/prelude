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
object Resource {

  def initResource(r : Resource,
                   scheme : String,
                   typ : String,
                   paths : ISeq[String],
                   relative : Boolean = false) : Unit =
    initResource(r, scheme, typ, paths,
      if (relative) paths.mkString("/")
      else getResourceUri(scheme, typ, paths).intern)

  def initResource(r : Resource,
                   scheme : String,
                   typ : String,
                   paths : ISeq[String],
                   uri : ResourceUri) : Unit = {
    r.uri(scheme, typ, paths, uri)
  }

  def getResourceUri(scheme : String, typ : String,
                     paths : ISeq[String], relative : Boolean = false) : ResourceUri =
    if (relative) UriUtil.uri(null, null, paths.mkString("/"), null)
    else UriUtil.uri(scheme, null, "/" + typ + "/" + paths.mkString("/"), null)
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
trait Resource {
  def uriScheme : String
  def uriType : String
  def uriPaths : ISeq[String]
  def uri : ResourceUri
  def hasResourceInfo : Boolean

  def uri(scheme : String, typ : String,
          paths : ISeq[String], uri : ResourceUri)
}

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
trait ResourceDefinition extends Resource

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
trait ResourceUser extends Resource
