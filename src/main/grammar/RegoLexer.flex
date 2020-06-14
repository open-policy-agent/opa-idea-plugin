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

ASCII_LETTER=[A-Za-z_][A-Za-z_0-9]*
NUMBER=-?(0|[1-9][0-9]*)(\.[0-9]+)?([eE][+-]?[0-9]*)?
WHITE_SPACE=[ \t\n\x0B\f\r]+
STRING_TOKEN=\"[^\"]*\"
RAW_STRING=`[^`]*`
COMMENT=[ \t]*#[^\r\n]*

%%
<YYINITIAL> {
  {WHITE_SPACE}       { return WHITE_SPACE; }

  "package"           { return PACKAGE_TOKEN; }
  "import"            { return IMPORT_TOKEN; }
  "as"                { return AS; }
  "default"           { return DEFAULT; }
  ":="                { return VAR_ASSIGN; }
  "="                 { return UNIFICATION; }
  "true"              { return TRUE; }
  "false"             { return FALSE; }
  "("                 { return LPAREN; }
  ")"                 { return RPAREN; }
  "["                 { return LBRACK; }
  "]"                 { return RBRACK; }
  "else"              { return ELSE; }
  "{"                 { return LBRACE; }
  "}"                 { return RBRACE; }
  ";"                 { return SEMICOLON; }
  "some"              { return SOME; }
  ","                 { return COMMA; }
  "."                 { return DOT; }
  "not"               { return NOT; }
  "with"              { return WITH; }
  ":"                 { return COLLON; }
  "=="                { return EQ; }
  "!="                { return NOT_EQ; }
  "<="                { return LESS_OR_EQUAL; }
  ">="                { return GREATER_OR_EQUAL; }
  ">"                 { return GREATHER; }
  "<"                 { return LESS; }
  "|"                 { return BIT_OR; }
  "&"                 { return BIT_AND; }
  "+"                 { return PLUS; }
  "-"                 { return MINUS; }
  "*"                 { return MUL; }
  "/"                 { return QUOTIENT; }
  "%"                 { return REMAINDER; }
  "set("              { return SET_OPEN; }
  "`"                 { return BACKSTRICK; }
  "null"              { return NULL; }

  {ASCII_LETTER}      { return ASCII_LETTER; }
  {NUMBER}            { return NUMBER; }
  {WHITE_SPACE}       { return WHITE_SPACE; }
  {STRING_TOKEN}      { return STRING_TOKEN; }
  {RAW_STRING}        { return RAW_STRING; }
  {COMMENT}           { return COMMENT; }

}

[^] { return BAD_CHARACTER; }
