/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.lsp

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl

class RegalLanguageClient(project: Project) : LanguageClientImpl(project) {
    companion object {
        private val LOG = Logger.getInstance(RegalLanguageClient::class.java)
    }

    /**
     * Custom client message handling can be added here for future Regal-specific features.
     *
     * To add custom message handling:
     * 1. Create an interface extending JsonSegment:
     *    @JsonSegment("regal")
     *    interface RegalLanguageClientExtensions {
     *        @JsonRequest("customMessage")
     *        fun customMessage(params: JsonObject): CompletableFuture<JsonObject>
     *    }
     * 2. Make this class implement the interface:
     *    class RegalLanguageClient(project: Project) : LanguageClientImpl(project), RegalLanguageClientExtensions
     * 3. Implement the custom message handler methods:
     *    override fun customMessage(params: JsonObject): CompletableFuture<JsonObject> {
     *        // Handle custom message
     *        return CompletableFuture.completedFuture(JsonObject())
     *    }
     */
}
