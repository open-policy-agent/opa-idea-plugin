package com.github.vgramer.opaplugin.ide.commenter

import com.intellij.lang.Commenter

class RegoCommenter : Commenter {
    override fun getCommentedBlockCommentPrefix(): String? = null

    override fun getCommentedBlockCommentSuffix(): String? = null

    override fun getBlockCommentPrefix(): String? = null

    override fun getBlockCommentSuffix(): String? = null

    override fun getLineCommentPrefix(): String = "#"

}