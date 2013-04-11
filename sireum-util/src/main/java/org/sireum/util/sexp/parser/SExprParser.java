// $ANTLR 3.3 Nov 30, 2010 12:45:30 /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g 2012-08-23 11:32:54

package org.sireum.util.sexp.parser;

/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/

import org.antlr.runtime.BitSet;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.RewriteRuleSubtreeStream;
import org.antlr.runtime.tree.RewriteRuleTokenStream;
import org.antlr.runtime.tree.TreeAdaptor;

public class SExprParser extends Parser {
  public static class exp_return extends ParserRuleReturnScope {
    Object tree;

    @Override
    public Object getTree() {
      return this.tree;
    }
  }

  public static class pexp_return extends ParserRuleReturnScope {
    Object tree;

    @Override
    public Object getTree() {
      return this.tree;
    }
  }

  public static class sexp_return extends ParserRuleReturnScope {
    Object tree;

    @Override
    public Object getTree() {
      return this.tree;
    }
  }

  public static class sexps_return extends ParserRuleReturnScope {
    Object tree;

    @Override
    public Object getTree() {
      return this.tree;
    }
  }

  public static final String[] tokenNames = new String[] { "<invalid>",
      "<EOR>", "<DOWN>", "<UP>", "EXP", "PEXP", "ATOM", "WS", "'('", "')'" };
  public static final int EOF = -1;
  public static final int T__8 = 8;
  public static final int T__9 = 9;

  // delegates
  // delegators

  public static final int EXP = 4;
  public static final int PEXP = 5;

  public static final int ATOM = 6;

  public static final int WS = 7;
  protected TreeAdaptor adaptor = new CommonTreeAdaptor();

  public static final BitSet FOLLOW_sexp_in_sexps52 = new BitSet(
      new long[] { 0x0000000000000142L });
  public static final BitSet FOLLOW_ATOM_in_sexp106 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_pexp_in_sexp112 = new BitSet(
      new long[] { 0x0000000000000002L });;

  public static final BitSet FOLLOW_8_in_pexp127 = new BitSet(
      new long[] { 0x0000000000000340L });

  public static final BitSet FOLLOW_exp_in_pexp129 = new BitSet(
      new long[] { 0x0000000000000340L });;

  public static final BitSet FOLLOW_9_in_pexp132 = new BitSet(
      new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_pexp_in_exp177 = new BitSet(
      new long[] { 0x0000000000000002L });;

  public static final BitSet FOLLOW_ATOM_in_exp182 = new BitSet(
      new long[] { 0x0000000000000002L });

  public SExprParser(final TokenStream input) {
    this(input, new RecognizerSharedState());
  };

  public SExprParser(final TokenStream input, final RecognizerSharedState state) {
    super(input, state);

  }

  // Delegated rules

  // $ANTLR start "exp"
  // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:50:1: exp : ( pexp | ATOM );
  public final SExprParser.exp_return exp() throws RecognitionException {
    final SExprParser.exp_return retval = new SExprParser.exp_return();
    retval.start = this.input.LT(1);

    Object root_0 = null;

    Token ATOM8 = null;
    SExprParser.pexp_return pexp7 = null;

    Object ATOM8_tree = null;

    try {
      // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:51:2: ( pexp | ATOM )
      int alt4 = 2;
      final int LA4_0 = this.input.LA(1);

      if ((LA4_0 == 8)) {
        alt4 = 1;
      } else if ((LA4_0 == SExprParser.ATOM)) {
        alt4 = 2;
      } else {
        final NoViableAltException nvae = new NoViableAltException("", 4, 0,
            this.input);

        throw nvae;
      }
      switch (alt4) {
        case 1:
        // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:51:4: pexp
        {
          root_0 = this.adaptor.nil();

          pushFollow(SExprParser.FOLLOW_pexp_in_exp177);
          pexp7 = pexp();

          this.state._fsp--;

          this.adaptor.addChild(root_0, pexp7.getTree());

        }
          break;
        case 2:
        // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:52:4: ATOM
        {
          root_0 = this.adaptor.nil();

          ATOM8 = (Token) match(
              this.input,
              SExprParser.ATOM,
              SExprParser.FOLLOW_ATOM_in_exp182);
          ATOM8_tree = this.adaptor.create(ATOM8);
          this.adaptor.addChild(root_0, ATOM8_tree);

        }
          break;

      }
      retval.stop = this.input.LT(-1);

      retval.tree = this.adaptor.rulePostProcessing(root_0);
      this.adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    } catch (final RecognitionException re) {
      reportError(re);
      recover(this.input, re);
      retval.tree = this.adaptor.errorNode(
          this.input,
          retval.start,
          this.input.LT(-1),
          re);

    } finally {
    }
    return retval;
  }

  // $ANTLR end "exp"
  @Override
  public String getGrammarFileName() {
    return "/Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g";
  }

  @Override
  public String[] getTokenNames() {
    return SExprParser.tokenNames;
  }

  public TreeAdaptor getTreeAdaptor() {
    return this.adaptor;
  }

  // $ANTLR start "pexp"
  // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:46:1: pexp : '(' ( exp )* ')' -> ^( PEXP ( exp )* ) ;
  public final SExprParser.pexp_return pexp() throws RecognitionException {
    final SExprParser.pexp_return retval = new SExprParser.pexp_return();
    retval.start = this.input.LT(1);

    Object root_0 = null;

    Token char_literal4 = null;
    Token char_literal6 = null;
    SExprParser.exp_return exp5 = null;

    final RewriteRuleTokenStream stream_9 = new RewriteRuleTokenStream(
        this.adaptor, "token 9");
    final RewriteRuleTokenStream stream_8 = new RewriteRuleTokenStream(
        this.adaptor, "token 8");
    final RewriteRuleSubtreeStream stream_exp = new RewriteRuleSubtreeStream(
        this.adaptor, "rule exp");
    try {
      // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:47:3: ( '(' ( exp )* ')' -> ^( PEXP ( exp )* ) )
      // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:47:5: '(' ( exp )* ')'
      {
        char_literal4 = (Token) match(
            this.input,
            8,
            SExprParser.FOLLOW_8_in_pexp127);
        stream_8.add(char_literal4);

        // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:47:9: ( exp )*
        loop3: do {
          int alt3 = 2;
          final int LA3_0 = this.input.LA(1);

          if (((LA3_0 == SExprParser.ATOM) || (LA3_0 == 8))) {
            alt3 = 1;
          }

          switch (alt3) {
            case 1:
            // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:47:9: exp
            {
              pushFollow(SExprParser.FOLLOW_exp_in_pexp129);
              exp5 = exp();

              this.state._fsp--;

              stream_exp.add(exp5.getTree());

            }
              break;

            default:
              break loop3;
          }
        } while (true);

        char_literal6 = (Token) match(
            this.input,
            9,
            SExprParser.FOLLOW_9_in_pexp132);
        stream_9.add(char_literal6);

        // AST REWRITE
        // elements: exp
        // token labels: 
        // rule labels: retval
        // token list labels: 
        // rule list labels: 
        // wildcard labels: 
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
            retval != null ? retval.tree : null);

        root_0 = this.adaptor.nil();
        // 47:40: -> ^( PEXP ( exp )* )
        {
          // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:47:43: ^( PEXP ( exp )* )
          {
            Object root_1 = this.adaptor.nil();
            root_1 = this.adaptor.becomeRoot(
                this.adaptor.create(SExprParser.PEXP, "PEXP"),
                root_1);

            // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:47:50: ( exp )*
            while (stream_exp.hasNext()) {
              this.adaptor.addChild(root_1, stream_exp.nextTree());

            }
            stream_exp.reset();

            this.adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = this.input.LT(-1);

      retval.tree = this.adaptor.rulePostProcessing(root_0);
      this.adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    } catch (final RecognitionException re) {
      reportError(re);
      recover(this.input, re);
      retval.tree = this.adaptor.errorNode(
          this.input,
          retval.start,
          this.input.LT(-1),
          re);

    } finally {
    }
    return retval;
  }

  // $ANTLR end "pexp"
  public void setTreeAdaptor(final TreeAdaptor adaptor) {
    this.adaptor = adaptor;
  }

  // $ANTLR start "sexp"
  // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:41:1: sexp : ( ATOM | pexp );
  public final SExprParser.sexp_return sexp() throws RecognitionException {
    final SExprParser.sexp_return retval = new SExprParser.sexp_return();
    retval.start = this.input.LT(1);

    Object root_0 = null;

    Token ATOM2 = null;
    SExprParser.pexp_return pexp3 = null;

    Object ATOM2_tree = null;

    try {
      // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:42:3: ( ATOM | pexp )
      int alt2 = 2;
      final int LA2_0 = this.input.LA(1);

      if ((LA2_0 == SExprParser.ATOM)) {
        alt2 = 1;
      } else if ((LA2_0 == 8)) {
        alt2 = 2;
      } else {
        final NoViableAltException nvae = new NoViableAltException("", 2, 0,
            this.input);

        throw nvae;
      }
      switch (alt2) {
        case 1:
        // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:42:5: ATOM
        {
          root_0 = this.adaptor.nil();

          ATOM2 = (Token) match(
              this.input,
              SExprParser.ATOM,
              SExprParser.FOLLOW_ATOM_in_sexp106);
          ATOM2_tree = this.adaptor.create(ATOM2);
          this.adaptor.addChild(root_0, ATOM2_tree);

        }
          break;
        case 2:
        // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:43:5: pexp
        {
          root_0 = this.adaptor.nil();

          pushFollow(SExprParser.FOLLOW_pexp_in_sexp112);
          pexp3 = pexp();

          this.state._fsp--;

          this.adaptor.addChild(root_0, pexp3.getTree());

        }
          break;

      }
      retval.stop = this.input.LT(-1);

      retval.tree = this.adaptor.rulePostProcessing(root_0);
      this.adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    } catch (final RecognitionException re) {
      reportError(re);
      recover(this.input, re);
      retval.tree = this.adaptor.errorNode(
          this.input,
          retval.start,
          this.input.LT(-1),
          re);

    } finally {
    }
    return retval;
  }

  // $ANTLR end "sexp"
  // $ANTLR start "sexps"
  // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:37:1: sexps : ( sexp )* -> ^( EXP ( sexp )* ) ;
  public final SExprParser.sexps_return sexps() throws RecognitionException {
    final SExprParser.sexps_return retval = new SExprParser.sexps_return();
    retval.start = this.input.LT(1);

    Object root_0 = null;

    SExprParser.sexp_return sexp1 = null;

    final RewriteRuleSubtreeStream stream_sexp = new RewriteRuleSubtreeStream(
        this.adaptor, "rule sexp");
    try {
      // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:38:3: ( ( sexp )* -> ^( EXP ( sexp )* ) )
      // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:38:5: ( sexp )*
      {
        // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:38:5: ( sexp )*
        loop1: do {
          int alt1 = 2;
          final int LA1_0 = this.input.LA(1);

          if (((LA1_0 == SExprParser.ATOM) || (LA1_0 == 8))) {
            alt1 = 1;
          }

          switch (alt1) {
            case 1:
            // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:38:5: sexp
            {
              pushFollow(SExprParser.FOLLOW_sexp_in_sexps52);
              sexp1 = sexp();

              this.state._fsp--;

              stream_sexp.add(sexp1.getTree());

            }
              break;

            default:
              break loop1;
          }
        } while (true);

        // AST REWRITE
        // elements: sexp
        // token labels: 
        // rule labels: retval
        // token list labels: 
        // rule list labels: 
        // wildcard labels: 
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
            retval != null ? retval.tree : null);

        root_0 = this.adaptor.nil();
        // 38:40: -> ^( EXP ( sexp )* )
        {
          // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:38:43: ^( EXP ( sexp )* )
          {
            Object root_1 = this.adaptor.nil();
            root_1 = this.adaptor.becomeRoot(
                this.adaptor.create(SExprParser.EXP, "EXP"),
                root_1);

            // /Users/robby/Repositories/sireum-internal/sireumv2/codebase/core/sireum-util/src/main/java/org/sireum/util/sexp/parser/SExpr.g:38:49: ( sexp )*
            while (stream_sexp.hasNext()) {
              this.adaptor.addChild(root_1, stream_sexp.nextTree());

            }
            stream_sexp.reset();

            this.adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = this.input.LT(-1);

      retval.tree = this.adaptor.rulePostProcessing(root_0);
      this.adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    } catch (final RecognitionException re) {
      reportError(re);
      recover(this.input, re);
      retval.tree = this.adaptor.errorNode(
          this.input,
          retval.start,
          this.input.LT(-1),
          re);

    } finally {
    }
    return retval;
  }
  // $ANTLR end "sexps"

}
