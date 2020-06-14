/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.opa.ideaplugin.lang.lexer

import com.intellij.lexer.FlexAdapter

/**
 * Adapt The JFlex lexer  to the IntelliJ Platform Lexer API.
 */
class RegoLexerAdapter : FlexAdapter(_RegoLexer())