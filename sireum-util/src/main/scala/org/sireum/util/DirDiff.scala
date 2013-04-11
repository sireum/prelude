/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

import java.io.File

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object DirDiff {
  def diff(source : File, target : File, removeFiles : Boolean = false) {
    for (fSource <- source.listFiles) {
      val fTarget = new File(target, fSource.getName)
      if (fSource.isDirectory) {
        if (fTarget.exists) {
          if (!removeFiles)
            println("Dir present: " + fTarget.getAbsolutePath)
          diff(fSource, fTarget, removeFiles)
          if (removeFiles && fTarget.list.length == 0) {
            println("Deleting dir: " + fTarget.getAbsolutePath)
            fTarget.delete
          }
        } else {
          println("Diff dir: " + fTarget.getAbsolutePath)
        }
      } else {
        if (fTarget.exists) {
          if (removeFiles) {
            println("Deleting file: " + fTarget.getAbsolutePath)
            fTarget.delete
          } else {
            println("File present: " + fTarget.getAbsolutePath)
          }
        } else
          println("Diff file: " + fTarget.getAbsolutePath)
      }
    }
  }

  def main(args : Array[String]) {
    diff(new File(args(0)), new File(args(1)), true)
  }
}