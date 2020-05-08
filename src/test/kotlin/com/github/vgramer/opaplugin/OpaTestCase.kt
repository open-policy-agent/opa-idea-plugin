package com.github.vgramer.opaplugin

import java.nio.file.Path

interface OpaTestCase {

    fun getTestDataPath(): String

    companion object {
        const val testResourcesPath = "src/test/resources"
    }
}


fun OpaTestCase.pathToSourceTestFile(name: String): Path =
    java.nio.file.Paths.get("${OpaTestCase.testResourcesPath}/${getTestDataPath()}/$name.rego")

fun OpaTestCase.pathToGoldTestFile(name: String): Path =
    java.nio.file.Paths.get("${OpaTestCase.testResourcesPath}/${getTestDataPath()}/$name.txt")