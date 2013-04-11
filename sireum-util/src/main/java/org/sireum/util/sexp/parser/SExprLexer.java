// $ANTLR 3.3 Nov 30, 2010 12:45:30 /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g 2012-08-23 11:32:54

package org.sireum.util.sexp.parser;

/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

public class SExprLexer extends Lexer {
  public static final int EOF = -1;
  public static final int T__8 = 8;
  public static final int T__9 = 9;
  public static final int EXP = 4;
  public static final int PEXP = 5;
  public static final int ATOM = 6;
  public static final int WS = 7;

  // delegates
  // delegators

  public SExprLexer() {
    ;
  }

  public SExprLexer(final CharStream input) {
    this(input, new RecognizerSharedState());
  }

  public SExprLexer(final CharStream input, final RecognizerSharedState state) {
    super(input, state);

  }

  @Override
  public String getGrammarFileName() {
    return "/Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g";
  }

  // $ANTLR start "ATOM"
  public final void mATOM() throws RecognitionException {
    try {
      final int _type = SExprLexer.ATOM;
      final int _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL;
      // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:56:3: ( (~ ( ' ' | '\\t' | '\\r' | '\\n' | '(' | ')' ) )+ )
      // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:56:5: (~ ( ' ' | '\\t' | '\\r' | '\\n' | '(' | ')' ) )+
      {
        // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:56:5: (~ ( ' ' | '\\t' | '\\r' | '\\n' | '(' | ')' ) )+
        int cnt1 = 0;
        loop1: do {
          int alt1 = 2;
          final int LA1_0 = this.input.LA(1);

          if ((((LA1_0 >= '\u0000') && (LA1_0 <= '\b'))
              || ((LA1_0 >= '\u000B') && (LA1_0 <= '\f'))
              || ((LA1_0 >= '\u000E') && (LA1_0 <= '\u001F'))
              || ((LA1_0 >= '!') && (LA1_0 <= '\'')) || ((LA1_0 >= '*') && (LA1_0 <= '\uFFFF')))) {
            alt1 = 1;
          }

          switch (alt1) {
            case 1:
            // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:56:5: ~ ( ' ' | '\\t' | '\\r' | '\\n' | '(' | ')' )
            {
              if (((this.input.LA(1) >= '\u0000') && (this.input.LA(1) <= '\b'))
                  || ((this.input.LA(1) >= '\u000B') && (this.input.LA(1) <= '\f'))
                  || ((this.input.LA(1) >= '\u000E') && (this.input.LA(1) <= '\u001F'))
                  || ((this.input.LA(1) >= '!') && (this.input.LA(1) <= '\''))
                  || ((this.input.LA(1) >= '*') && (this.input.LA(1) <= '\uFFFF'))) {
                this.input.consume();

              } else {
                final MismatchedSetException mse = new MismatchedSetException(
                    null, this.input);
                recover(mse);
                throw mse;
              }

            }
              break;

            default:
              if (cnt1 >= 1) {
                break loop1;
              }
              final EarlyExitException eee = new EarlyExitException(1,
                  this.input);
              throw eee;
          }
          cnt1++;
        } while (true);

      }

      this.state.type = _type;
      this.state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "ATOM"

  // $ANTLR start "T__8"
  public final void mT__8() throws RecognitionException {
    try {
      final int _type = SExprLexer.T__8;
      final int _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL;
      // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:15:6: ( '(' )
      // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:15:8: '('
      {
        match('(');

      }

      this.state.type = _type;
      this.state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "T__8"

  // $ANTLR start "T__9"
  public final void mT__9() throws RecognitionException {
    try {
      final int _type = SExprLexer.T__9;
      final int _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL;
      // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:16:6: ( ')' )
      // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:16:8: ')'
      {
        match(')');

      }

      this.state.type = _type;
      this.state.channel = _channel;
    } finally {
    }
  }

  // $ANTLR end "T__9"

  @Override
  public void mTokens() throws RecognitionException {
    // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:1:8: ( T__8 | T__9 | ATOM | WS )
    int alt2 = 4;
    final int LA2_0 = this.input.LA(1);

    if ((LA2_0 == '(')) {
      alt2 = 1;
    } else if ((LA2_0 == ')')) {
      alt2 = 2;
    } else if ((((LA2_0 >= '\u0000') && (LA2_0 <= '\b'))
        || ((LA2_0 >= '\u000B') && (LA2_0 <= '\f'))
        || ((LA2_0 >= '\u000E') && (LA2_0 <= '\u001F'))
        || ((LA2_0 >= '!') && (LA2_0 <= '\'')) || ((LA2_0 >= '*') && (LA2_0 <= '\uFFFF')))) {
      alt2 = 3;
    } else if ((((LA2_0 >= '\t') && (LA2_0 <= '\n')) || (LA2_0 == '\r') || (LA2_0 == ' '))) {
      alt2 = 4;
    } else {
      final NoViableAltException nvae = new NoViableAltException("", 2, 0,
          this.input);

      throw nvae;
    }
    switch (alt2) {
      case 1:
      // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:1:10: T__8
      {
        mT__8();

      }
        break;
      case 2:
      // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:1:15: T__9
      {
        mT__9();

      }
        break;
      case 3:
      // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:1:20: ATOM
      {
        mATOM();

      }
        break;
      case 4:
      // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:1:25: WS
      {
        mWS();

      }
        break;

    }

  }

  // $ANTLR start "WS"
  public final void mWS() throws RecognitionException {
    try {
      final int _type = SExprLexer.WS;
      int _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL;
      // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:66:3: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
      // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:66:5: ( ' ' | '\\t' | '\\r' | '\\n' )
      {
        if (((this.input.LA(1) >= '\t') && (this.input.LA(1) <= '\n'))
            || (this.input.LA(1) == '\r') || (this.input.LA(1) == ' ')) {
          this.input.consume();

        } else {
          final MismatchedSetException mse = new MismatchedSetException(null,
              this.input);
          recover(mse);
          throw mse;
        }

        _channel = BaseRecognizer.HIDDEN;

      }

      this.state.type = _type;
      this.state.channel = _channel;
    } finally {
    }
  }
  // $ANTLR end "WS"

}
