/*
Copyright (c) 2014 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util

import rx.lang.scala._
import java.util.concurrent._

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
final class RxAsyncQueue[E] {
  private val queue = new LinkedBlockingQueue[Either[Object, E]]

  import language.implicitConversions
  private implicit def econv(e : E) = Right(e)

  def observe =
    Observable({ sub : Subscriber[E] =>
      (new Thread {
        override def run {
          var term = false
          while (!term && !sub.isUnsubscribed) {
            queue.take match {
              case Left(_) => sub.onCompleted; term = true
              case Right(t) => sub.onNext(t)
            }
          }
        }
      }).start
    })

  def add(e : E) = queue.add(e)

  def offer(e : E) = queue.offer(e)

  def put(e : E) {
    queue.put(e)
  }

  def offer(e : E, timeout : Long, unit : TimeUnit) =
    queue.offer(e, timeout, unit)

  def contains(e : E) = queue.contains(Right(e))

  def remainingCapacity = queue.remainingCapacity

  def stop {
    queue.add(Left("stop"))
  }
}