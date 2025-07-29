/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

// Implementation based on LSP4IJ Developer Guide:
// https://github.com/redhat-developer/lsp4ij/blob/035c4fd82cc7603ef3a346be0d9585d4e41564b0/docs/dap/DeveloperGuide.md

package org.openpolicyagent.ideaplugin.lsp

import com.intellij.openapi.project.Project
import com.intellij.openapi.diagnostic.Logger
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider
import org.jetbrains.annotations.NotNull

class RegalLanguageServerFactory : LanguageServerFactory {
    override fun createConnectionProvider(@NotNull project: Project): StreamConnectionProvider {
        return RegalStreamConnectionProvider(project)
    }

    override fun createLanguageClient(@NotNull project: Project): LanguageClientImpl {
        return RegalLanguageClient(project)
    }
}
