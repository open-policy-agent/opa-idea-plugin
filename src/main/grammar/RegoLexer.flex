package org.opa.ideaplugin.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static org.opa.ideaplugin.lang.psi.RegoTypes.*;

%%

%{
  public _RegoLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _RegoLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

STRING_LITERAL=(\"[^\"\\]*(\\.[^\"\\]*)*\")|(`[^`]*`)
NUMBER=-?(0|([1-9][0-9]*))(\.[0-9]+)?([eE][+-]?[0-9]+)?
COMMENT=#.*
VAR=[\p{Alpha}_][\p{Alnum}_]*

%%
<YYINITIAL> {
  {WHITE_SPACE}         { return WHITE_SPACE; }

  "true"                { return TRUE_KW; }
  "false"               { return FALSE_KW; }
  "null"                { return NULL_KW; }
  "package"             { return PACKAGE_KW; }
  "import"              { return IMPORT_KW; }
  "as"                  { return AS_KW; }
  "default"             { return DEFAULT_KW; }
  "else"                { return ELSE_KW; }
  "not"                 { return NOT_KW; }
  "with"                { return WITH_KW; }
  "some"                { return SOME_KW; }
  ":="                  { return ASSIGN; }
  "="                   { return UNIFY; }
  "|"                   { return OR; }
  "&"                   { return AND; }
  "+"                   { return PLUS; }
  "-"                   { return MINUS; }
  "*"                   { return MULTIPLY; }
  "/"                   { return DIVIDE; }
  "%"                   { return REMAINDER; }
  "=="                  { return EQ; }
  "!="                  { return NEQ; }
  "<"                   { return LT; }
  ">"                   { return GT; }
  "<="                  { return LTE; }
  ">="                  { return GTE; }
  "("                   { return LPAREN; }
  ")"                   { return RPAREN; }
  "{"                   { return LBRACE; }
  "}"                   { return RBRACE; }
  "["                   { return LBRACK; }
  "]"                   { return RBRACK; }
  "set"                 { return SET_KW; }
  ","                   { return COMMA; }
  ";"                   { return SEMIC; }
  ":"                   { return COLON; }
  "`"                   { return BACKTICK; }
  "."                   { return DOT; }
  "_"                   { return UNDER; }

  {STRING_LITERAL}      { return STRING_LITERAL; }
  {NUMBER}              { return NUMBER; }
  {COMMENT}             { return COMMENT; }
  {VAR}                 { return VAR; }

}

[^] { return BAD_CHARACTER; }
