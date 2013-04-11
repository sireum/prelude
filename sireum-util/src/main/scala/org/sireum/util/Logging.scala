/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory

object LoggingUtil {
  def logger(o : AnyRef) = Logger(LoggerFactory getLogger o.getClass.getName)
}

trait ImplicitLogging {
  implicit lazy val logger = Logger(LoggerFactory getLogger getClass.getName)
}