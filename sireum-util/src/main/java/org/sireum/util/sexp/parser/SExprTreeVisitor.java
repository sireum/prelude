/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

package org.sireum.util.sexp.parser;

import org.antlr.runtime.tree.Tree;

public class SExprTreeVisitor<G> {

  G context;

  public SExprTreeVisitor(final G context) {
    this.context = context;
  }

  protected boolean defaultCase(final Tree t) {
    return true;
  }

  public void visit(final Tree t) {
    switch (t.getType()) {
      case 6:
        if (visitATOM(t)) {
          visitChildren(t);
        }
        return;
      case 7:
        if (visitWS(t)) {
          visitChildren(t);
        }
        return;
      case 4:
        if (visitEXP(t)) {
          visitChildren(t);
        }
        return;
      case 9:
        if (visitT__9(t)) {
          visitChildren(t);
        }
        return;
      case 8:
        if (visitT__8(t)) {
          visitChildren(t);
        }
        return;
      case 5:
        if (visitPEXP(t)) {
          visitChildren(t);
        }
        return;
      default:
        defaultCase(t);
    }
  }

  protected boolean visitATOM(final Tree t) {
    return defaultCase(t);
  }

  protected void visitChildren(final Tree t) {
    final int count = t.getChildCount();
    for (int i = 0; i < count; i++) {
      visit(t.getChild(i));
    }
  }

  protected boolean visitEXP(final Tree t) {
    return defaultCase(t);
  }

  protected boolean visitPEXP(final Tree t) {
    return defaultCase(t);
  }

  protected boolean visitT__8(final Tree t) {
    return defaultCase(t);
  }

  protected boolean visitT__9(final Tree t) {
    return defaultCase(t);
  }

  protected boolean visitWS(final Tree t) {
    return defaultCase(t);
  }
}
