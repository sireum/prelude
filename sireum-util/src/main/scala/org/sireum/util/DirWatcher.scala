/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sireum.util

import java.nio.file._
import java.nio.file.attribute._
import java.nio.file.StandardWatchEventKinds._
import rx.lang.scala._
import java.util.concurrent.TimeUnit

object DirWatcher {
  def apply(p : Path, recursive : Boolean = true, timeout : Int = 1) =
    new DirWatcher(p, recursive, timeout)
}

/**
 * Adapted by <a href="mailto:robby@k-state.edu">Robby</a> from
 * Java Tutorials Code Sample – <a href="http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java">WatchDir.java</a>
 */
final class DirWatcher(base : Path, recursive : Boolean, timeout : Int) {
  @volatile
  private var term = false
  val watcher = FileSystems.getDefault.newWatchService
  val keys = mmapEmpty[WatchKey, Path]

  {
    if (Files.exists(base) && Files.isDirectory(base))
      if (recursive)
        registerAll(base)
      else
        register(base)
  }

  private def register(d : Path) {
    val key =
      if (Files.isDirectory(d))
        d.register(watcher, ENTRY_CREATE, ENTRY_DELETE)
      else
        d.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)
    keys(key) = d
  }

  private def walkFileTree(d : Path, f : Path => Unit, isDir : Boolean) {
    Files.walkFileTree(d, new SimpleFileVisitor[Path] {
      override def preVisitDirectory(
        d : Path, attrs : BasicFileAttributes) = {
        f(d)
        FileVisitResult.CONTINUE
      }
      override def visitFile(
        p : Path, attrs : BasicFileAttributes) = {
        if (!isDir) f(p)
        FileVisitResult.CONTINUE
      }
    })
  }

  private def registerAll(d : Path) {
    walkFileTree(d, register, true)
  }

  val observe =
    Observable({ sub : Subscriber[(WatchEvent.Kind[Path], Path)] =>
      (new Thread {
        override def run {
          while (!term && !sub.isUnsubscribed) {
            import scala.collection.JavaConversions._
            val key = watcher.poll(timeout, TimeUnit.SECONDS)
            keys.get(key) match {
              case Some(d) =>
                for (event <- key.pollEvents if event.kind != OVERFLOW) {
                  val e = event.asInstanceOf[WatchEvent[Path]]
                  val p = d.resolve(e.context)
                  try {
                    if (recursive && (e.kind == ENTRY_CREATE) &&
                      Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS)) {
                      walkFileTree(p, { p =>
                        if (!sub.isUnsubscribed)
                          sub.onNext((e.kind, p))
                      }, false)
                      registerAll(p)
                    } else if (!sub.isUnsubscribed)
                      sub.onNext((e.kind, p))
                  } catch {
                    case _ : Exception =>
                  }
                }
                if (!key.reset) {
                  keys.remove(key)
                  if (keys.isEmpty) {
                    term = true
                    sub.onCompleted
                  }
                }
              case _ =>
            }
          }
        }
      }).start
    })

  def stop { term = true }
}