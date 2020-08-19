package org.openpolicyagent.ideaplugin.ide.extensions
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.openapi.util.Key
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import org.json.JSONArray
import org.json.JSONObject

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
            val json = JSONObject(output)
            if (json.has("result")){
                val results = json.getJSONArray("result")
                val first = results.getJSONObject(0)
                val screen_output = JSONArray()
                if (first.has("bindings")) {
                    val bindings = first.getJSONArray("bindings")
                    for (i in 0 until results.length()) {
                        if(results.getJSONObject(i).has("bindings")){
                            screen_output.put(results.getJSONObject(i).get("bindings"))
                        }
                    }

                } else {
                    for (i in 0 until results.length()) {
                        if(results.getJSONObject(i).has("expressions")){
                            val expressions = results.getJSONObject(i).getJSONArray("expressions")
                            val sublist = JSONArray()
                            for (j in 0 until expressions.length()){
                                sublist.put(expressions.getJSONObject(j).get("value"))
                            }
                            screen_output.put(sublist)
                        }
                    }
                }
                consoleView.print("${screen_output.toString(4)}", ConsoleViewContentType.LOG_INFO_OUTPUT)
            } else {
                consoleView.print("No results found", ConsoleViewContentType.LOG_INFO_OUTPUT)
            }
        }
    }


    override fun processWillTerminate(event: ProcessEvent, willBeDestroyed: Boolean) {
    }

    override fun startNotified(event: ProcessEvent) {
        output = ""
    }


}