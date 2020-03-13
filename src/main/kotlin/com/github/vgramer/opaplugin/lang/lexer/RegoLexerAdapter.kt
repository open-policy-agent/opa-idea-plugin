package com.github.vgramer.opaplugin.lang.lexer

import com.intellij.lexer.FlexAdapter

/**
 * Adapt The JFlex lexer  to the IntelliJ Platform Lexer API.
 */
class RegoLexerAdapter : FlexAdapter(_RegoLexer())