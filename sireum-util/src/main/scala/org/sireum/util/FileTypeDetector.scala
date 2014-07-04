/*
Copyright (c) 2014 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

import org.apache.tika._

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
class FileTypeDetector extends java.nio.file.spi.FileTypeDetector {
  final val tika = new Tika

  def probeContentType(path : java.nio.file.Path) : String = {
    tika.detect(path.toFile)
  }
}