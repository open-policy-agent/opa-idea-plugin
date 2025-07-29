/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.lsp

import com.google.gson.JsonObject
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment
import java.util.concurrent.CompletableFuture

@JsonSegment("regal")
interface RegalLanguageClientExtensions {
    @JsonRequest("startDebugging")
    fun startDebugging(params: JsonObject): CompletableFuture<JsonObject>
}

class RegalLanguageClient(project: Project) : LanguageClientImpl(project), RegalLanguageClientExtensions {
    companion object {
        private val LOG = Logger.getInstance(RegalLanguageClient::class.java)
    }

    override fun startDebugging(params: JsonObject): CompletableFuture<JsonObject> {
        LOG.info("RegalLanguageClient: startDebugging called with params: $params")
        
        // Debugging is now handled via DAP integration
        // This LSP method is kept for compatibility but redirects to DAP
        val response = JsonObject()
        response.addProperty("status", "redirected")
        response.addProperty("message", "Debug requests are now handled via DAP. Use IntelliJ's Debug menu to start debugging.")
        
        return CompletableFuture.completedFuture(response)
    }
}