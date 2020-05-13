/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.github.vgramer.opaplugin.lang.parser

import com.github.vgramer.opaplugin.lang.RegoLanguage
import com.github.vgramer.opaplugin.lang.lexer.RegoLexerAdapter
import com.github.vgramer.opaplugin.lang.psi.RegoFile
import com.github.vgramer.opaplugin.lang.psi.RegoTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.ParserDefinition.SpaceRequirements
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet


class RegoParserDefinition : ParserDefinition {
    override fun createLexer(project: Project): Lexer = RegoLexerAdapter()

    override fun createParser(project: Project): PsiParser = RegoParser()

    override fun getWhitespaceTokens(): TokenSet = WHITE_SPACES

    override fun getCommentTokens(): TokenSet = COMMENTS

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun getFileNodeType(): IFileElementType = FILE

    override fun createFile(viewProvider: FileViewProvider): PsiFile = RegoFile(viewProvider)

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode, right: ASTNode): SpaceRequirements = SpaceRequirements.MAY

    override fun createElement(node: ASTNode): PsiElement = RegoTypes.Factory.createElement(node)

    companion object {
        val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
        val COMMENTS = TokenSet.create(RegoTypes.COMMENT)
        val FILE = IFileElementType(RegoLanguage)
    }
}
