package org.openpolicyagent.ideaplugin.ide.extensions
import com.beust.klaxon.JsonArray
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.openapi.util.Key
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import org.mozilla.javascript.NativeJSON.stringify

class EvalListener(consoleView: ConsoleView): ProcessListener {
    private var output = ""
    private val consoleView = consoleView

    override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
        output="$output${event.text}"
    }

    override fun processTerminated(event: ProcessEvent) {
        val exitcode = event.exitCode
        if (exitcode != 0) {
            consoleView.print(output, ConsoleViewContentType.ERROR_OUTPUT)
        } else {
            val parser = Parser.default()
            val stringBuilder: StringBuilder = StringBuilder(output)
            val json: JsonObject = parser.parse(stringBuilder) as JsonObject
            //get json.result[0]
            val results: JsonArray<JsonObject>? = json.array("result")
            if (results != null){
                val first = results[0]
                consoleView.print("${first.toJsonString(true)}", ConsoleViewContentType.LOG_DEBUG_OUTPUT)
                val bindings : JsonArray<JsonObject>? = first.array("bindings")
                if (bindings == null) {
                    consoleView.print("Bindings null", ConsoleViewContentType.LOG_DEBUG_OUTPUT)
                    val screen_output = results.map {
                       val expressions : JsonArray<JsonObject>? = it.array("expressions")
                        var test = ""
                        expressions?.map {
                           it.boolean("value")
                       }
                   }
                    consoleView.print("${stringify(screen_output)}", ConsoleViewContentType.LOG_INFO_OUTPUT)
                } else {
                    consoleView.print("$bindings", ConsoleViewContentType.LOG_DEBUG_OUTPUT)
                    //output = result.result.map((x: any) => x.bindings);
                    results.map{
                        val indiv_bindings: JsonArray<Boolean>? = it.array("bindings")
                        indiv_bindings?.map{
                            consoleView.print(it.toString(), ConsoleViewContentType.LOG_INFO_OUTPUT)
                        }
                    }

                }
            } else {
                consoleView.print("No results found", ConsoleViewContentType.LOG_INFO_OUTPUT)
            }
        }
            //consoleView.print("${json.string("result")}", ConsoleViewContentType.NORMAL_OUTPUT)
    }


    override fun processWillTerminate(event: ProcessEvent, willBeDestroyed: Boolean) {
        print("processWillTerminate")
        print(event.text)
    }

    override fun startNotified(event: ProcessEvent) {
        output = ""

    }


}