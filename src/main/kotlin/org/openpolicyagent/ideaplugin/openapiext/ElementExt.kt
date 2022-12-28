/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

// Important NOTE: These methods been borrowed from IntelliJ rust plugin

package org.openpolicyagent.ideaplugin.openapiext

import com.intellij.execution.ExternalizablePath
import com.intellij.openapi.util.JDOMUtil
import org.jdom.Element
import java.nio.file.Path
import java.nio.file.Paths

// Sets of utility method used to persist RunConfigurations

fun Element.writeString(name: String, value: String?) {
    val opt = Element("option")
    opt.setAttribute("name", name)
    opt.setAttribute("value", value ?: "")
    addContent(opt)
}


fun Element.readString(name: String): String? =
    children
        .find { it.name == "option" && it.getAttributeValue("name") == name }
        ?.getAttributeValue("value")


fun Element.writePath(name: String, value: Path?) {
    if (value != null) {
        val s = ExternalizablePath.urlValue(value.toString())
        writeString(name, s)
    }
}

fun Element.readPath(name: String): Path? {
    return readString(name)?.let { Paths.get(ExternalizablePath.localPathValue(it)) }
}

fun Element.toXmlString() = JDOMUtil.writeElement(this)

