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
object TupleHelper {
  def makeTuple(elems : CSeq[Any]) : Product = {
    elems.size match {
      case 1 => Tuple1(elems(0))
      case 2 => Tuple2(elems(0), elems(1))
      case 3 => Tuple3(elems(0), elems(1), elems(2))
      case 4 => Tuple4(elems(0), elems(1), elems(2), elems(3))
      case 5 => Tuple5(elems(0), elems(1), elems(2), elems(3), elems(4))
      case 6 => Tuple6(elems(0), elems(1), elems(2), elems(3), elems(4), elems(5))
      case 7 => Tuple7(elems(0), elems(1), elems(2), elems(3), elems(4), elems(5),
        elems(6))
      case 8 => Tuple8(elems(0), elems(1), elems(2), elems(3), elems(4), elems(5),
        elems(6), elems(7))
      case 9 => Tuple9(elems(0), elems(1), elems(2), elems(3), elems(4), elems(5),
        elems(6), elems(7), elems(8))
      case 10 => Tuple10(elems(0), elems(1), elems(2), elems(3), elems(4), elems(5),
        elems(6), elems(7), elems(8), elems(9))
      case 11 => Tuple11(elems(0), elems(1), elems(2), elems(3), elems(4), elems(5),
        elems(6), elems(7), elems(8), elems(9), elems(10))
      case 12 => Tuple12(elems(0), elems(1), elems(2), elems(3), elems(4), elems(5),
        elems(6), elems(7), elems(8), elems(9), elems(10), elems(11))
      case 13 => Tuple13(elems(0), elems(1), elems(2), elems(3), elems(4), elems(5),
        elems(6), elems(7), elems(8), elems(9), elems(10), elems(11),
        elems(12))
      case 14 => Tuple14(elems(0), elems(1), elems(2), elems(3), elems(4), elems(5),
        elems(6), elems(7), elems(8), elems(9), elems(10), elems(11),
        elems(12), elems(13))
      case 15 => Tuple15(elems(0), elems(1), elems(2), elems(3), elems(4), elems(5),
        elems(6), elems(7), elems(8), elems(9), elems(10), elems(11),
        elems(12), elems(13), elems(14))
      case 16 => Tuple16(elems(0), elems(1), elems(2), elems(3), elems(4), elems(5),
        elems(6), elems(7), elems(8), elems(9), elems(10), elems(11),
        elems(12), elems(13), elems(14), elems(15))
      case 17 => Tuple17(elems(0), elems(1), elems(2), elems(3), elems(4), elems(5),
        elems(6), elems(7), elems(8), elems(9), elems(10), elems(11),
        elems(12), elems(13), elems(14), elems(15), elems(16))
      case 18 => Tuple18(elems(0), elems(1), elems(2), elems(3), elems(4), elems(5),
        elems(6), elems(7), elems(8), elems(9), elems(10), elems(11),
        elems(12), elems(13), elems(14), elems(15), elems(16), elems(17))
      case 19 => Tuple19(elems(0), elems(1), elems(2), elems(3), elems(4), elems(5),
        elems(6), elems(7), elems(8), elems(9), elems(10), elems(11),
        elems(12), elems(13), elems(14), elems(15), elems(16), elems(17),
        elems(18))
      case 20 => Tuple20(elems(0), elems(1), elems(2), elems(3), elems(4), elems(5),
        elems(6), elems(7), elems(8), elems(9), elems(10), elems(11),
        elems(12), elems(13), elems(14), elems(15), elems(16), elems(17),
        elems(18), elems(19))
      case 21 => Tuple21(elems(0), elems(1), elems(2), elems(3), elems(4), elems(5),
        elems(6), elems(7), elems(8), elems(9), elems(10), elems(11),
        elems(12), elems(13), elems(14), elems(15), elems(16), elems(17),
        elems(18), elems(19), elems(20))
      case 22 => Tuple22(elems(0), elems(1), elems(2), elems(3), elems(4), elems(5),
        elems(6), elems(7), elems(8), elems(9), elems(10), elems(11),
        elems(12), elems(13), elems(14), elems(15), elems(16), elems(17),
        elems(18), elems(19), elems(20), elems(21))
    }
  }
}