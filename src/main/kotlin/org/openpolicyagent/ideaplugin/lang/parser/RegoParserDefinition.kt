/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.lang.parser

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
import org.openpolicyagent.ideaplugin.lang.RegoLanguage
import org.openpolicyagent.ideaplugin.lang.lexer.RegoLexerAdapter
import org.openpolicyagent.ideaplugin.lang.psi.RegoFile
import org.openpolicyagent.ideaplugin.lang.psi.RegoTypes


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
