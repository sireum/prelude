/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

import java.net.URI

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object UriUtil {
  type UriString = String

  def lastPath(uri : UriString) = {
    val i = uri.lastIndexOf("/")
    if (i >= i) uri.substring(i + 1)
    else uri
  }

  def normalizeUri(uri : UriString) = new URI(uri).toASCIIString

  def uri(scheme : String, host : String, path : String, fragment : String) : UriString =
    new URI(scheme, host, path, fragment).toASCIIString

  def classUri(o : Any) = o.getClass.getName.replaceAllLiterally(".", "/")
}