grammar SExpr;

options { output=AST; }

tokens
{
  EXP;
  PEXP;
}

@header
{
package org.sireum.util.sexp.parser;

/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/
}

@lexer::header
{ 
package org.sireum.util.sexp.parser;

/*
Copyright (c) 2011-2013 Robby, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/
}

sexps
  : sexp*	                             -> ^(EXP sexp*)
  ;
  
sexp
  : ATOM
  | pexp
  ;
  
pexp
  :	'(' exp* ')'                       -> ^(PEXP exp*)
  ;
  
exp
	: pexp
	| ATOM
	;

ATOM
  : ~ ( ' '
      | '\t'
      | '\r'
      | '\n'
      | '('
      | ')'
      )+  	
  ;

WS
  : ( ' '
    | '\t'
    | '\r'
    | '\n'
    ) {$channel=HIDDEN;}
	;

