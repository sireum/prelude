package org.sireum.util

/**
 * @author <a href="mailto:robby@k-state.edu">Robby</a>
 */
object ImmutableUtil {

  def bopa[I <: Immutable, R](beforeF : I => I)(op : I => (I => I) => R)(afterF : I => I)(o : I) : R =
    op(beforeF(o))(afterF)
}