package org.sireum.util

object ProductUtil {

  private val consCache =
    new scala.collection.mutable.WeakHashMap[java.lang.Class[_], // 
    java.lang.reflect.Constructor[_]]

  def make[T](pClass : Class[T], elements : Object*) : T = {
    val cons = consCache.getOrElseUpdate(pClass, pClass.getConstructors()(0))
    val result = cons.newInstance(elements : _*)
    result.asInstanceOf[T]
  }
}