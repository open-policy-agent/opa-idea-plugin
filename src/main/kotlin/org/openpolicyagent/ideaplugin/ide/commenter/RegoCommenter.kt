/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.commenter

import com.intellij.lang.Commenter

/**
 * Defines the support for "Comment with Line Comment" (eg '//' in java)  and "Comment with Block Comment" (eg '/* */')
 * This class implements comment / uncomment code feature.
 *
 * note: Rego has only one type of comment: LineComment ( you can only comment code with '#')
 */
class RegoCommenter : Commenter {
    override fun getCommentedBlockCommentPrefix(): String? = null

    override fun getCommentedBlockCommentSuffix(): String? = null

    override fun getBlockCommentPrefix(): String? = null

    override fun getBlockCommentSuffix(): String? = null

    override fun getLineCommentPrefix(): String = "#"

}
