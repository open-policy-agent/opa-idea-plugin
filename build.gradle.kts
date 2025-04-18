/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

import org.intellij.markdown.ast.getTextInNode
import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.intellij.platform.gradle.TestFrameworkType


val baseIDE = prop("baseIDE")

val ideType = when (baseIDE) {
    "idea" -> "IC"
    "pycharmCommunity" -> "PC"
    else -> error("Unexpected IDE name: `$baseIDE`")
}
val ideVersion = when (baseIDE) {
    "idea" -> prop("ideaVersion")
    "pycharmCommunity" -> prop("pycharmCommunityVersion")
    else -> error("Unexpected IDE name: `$baseIDE`")
}

val psiViewerPluginVersion = prop("psiViewerPluginVersion")
val channel = prop("publishChannel")

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        // needed to extract the last release notes
        classpath("org.jetbrains:markdown:0.2.0")
    }
}

idea {
    module {
        // https://github.com/gradle/kotlin-dsl/issues/537/
        excludeDirs = excludeDirs + file("testData")
    }
}

plugins {
    idea
    kotlin("jvm") version "1.9.21"
    id("org.jetbrains.intellij.platform.module") version "2.1.0"
    id("org.jetbrains.grammarkit") version "2022.3.2.2"

}

allprojects {
    apply {
        plugin("idea")
        plugin("kotlin")
        plugin("org.jetbrains.grammarkit")
        plugin("org.jetbrains.intellij.platform.module")
    }

    repositories {
        mavenCentral()
        intellijPlatform {
            defaultRepositories()
        }
    }

    configurations {
        all {
            resolutionStrategy.sortArtifacts(ResolutionStrategy.SortOrder.DEPENDENCY_FIRST)
        }
    }

    dependencies {
        testImplementation("junit", "junit", "4.13.2")
        implementation("com.github.kittinunf.fuel", "fuel", "2.3.1") {
            exclude("org.jetbrains.kotlin")
        }
        testImplementation("org.assertj:assertj-core:3.24.2")

        intellijPlatform {
            create(ideType, ideVersion)
            val pluginList = mutableListOf(
                "PsiViewer:$psiViewerPluginVersion"
            )
            plugins(pluginList)

            if (baseIDE == "idea") {
                bundledPlugin("com.intellij.java")
            }
            instrumentationTools()
            pluginVerifier()
            testFramework(TestFrameworkType.Platform)
        }
    }

    idea {
        module {
            generatedSourceDirs.add(file("src/main/gen"))
        }
    }

//    intellijPlatform {
//        sandboxContainer = file("$buildDir/$baseIDE-sandbox-$baseVersion")
//    }
    intellijPlatform {
        buildSearchableOptions = false
        instrumentCode = true
    }

    // Set the JVM language level used to build project. Use Java 11 for 2020.3+, and Java 17 for 2022.2+.
    kotlin {
        jvmToolchain(17)
    }

    sourceSets {
        main {
            java.srcDirs("src/main/gen")
        }
    }

    afterEvaluate {
        tasks.withType<Test>().configureEach {
            // We need to prevent the platform-specific shared JNA library to loading from the system library paths,
            // because otherwise it can lead to compatibility issues.
            // Also note that IDEA does the same thing at startup, and not only for tests.
            systemProperty("jna.nosys", "true")
        }
    }
}

val channelSuffix = if (channel.isBlank() || channel == "stable") "" else "-$channel"
val pluginVersion = prop("pluginVersion")


// module to build/run/publish opa-ida-plugin plugin
project(":plugin") {
    apply {
        plugin("org.jetbrains.intellij.platform")
    }

    intellijPlatform {
        instrumentCode = true
        buildSearchableOptions = false
        projectName.set("opa-idea-plugin")
        pluginConfiguration {
            name = "Open Policy Agent"
            version = "$pluginVersion$channelSuffix"

            ideaVersion {
                sinceBuild = providers.gradleProperty("sinceBuild")
                untilBuild = providers.gradleProperty("untilBuild")
            }
            changeNotes = getLastReleaseNotes()
        }
        pluginVerification {
            ides {
                recommended()
            }
        }
        publishing {
            token = prop("publishToken")
            channels = listOf(channel)
        }
    }

    tasks.withType<Zip>().configureEach {
        if (name == "buildPlugin") {
            archiveVersion.set(pluginVersion + channelSuffix)
        }
    }

    dependencies {
        intellijPlatform {
            pluginModule(implementation(project(":")))
            pluginModule(implementation(project(":idea")))
        }
    }
}

project(":") {
    apply {
        plugin("org.jetbrains.intellij.platform.module")
    }
    val testOutput = configurations.create("testOutput")

    dependencies {
        testOutput(sourceSets.getByName("test").output.classesDirs)
    }

    val generateRegoLexer = task<GenerateLexerTask>("generateRegoLexer") {
        sourceFile.set(file("src/main/grammar/RegoLexer.flex"))
        targetOutputDir.set(project.layout.projectDirectory.dir("src/main/gen/org/openpolicyagent/ideaplugin/lang/lexer"))
        purgeOldFiles.set(true)
    }


    val generateRegoParser = task<GenerateParserTask>("generateRegoParser") {
        sourceFile.set(file("src/main/grammar/Rego.bnf"))
        targetRootOutputDir.set(project.layout.projectDirectory.dir("src/main/gen"))
        pathToParser.set("/org/openpolicyagent/ideaplugin/lang/parser/RegoParser.java")
        pathToPsiRoot.set("/org/openpolicyagent/ideaplugin/lang/psi")
        purgeOldFiles.set(true)
    }

    tasks.withType<KotlinCompile> {
        dependsOn(
            generateRegoLexer,
            generateRegoParser
        )
    }
}

project(":idea") {
    apply {
        plugin("org.jetbrains.intellij.platform.module")
    }
    dependencies {
        intellijPlatform {
            pluginModule(implementation(project(":")))
            testImplementation(project(":", "testOutput"))
        }

    }
}

fun prop(name: String): String =
    extra.properties[name] as? String
        ?: error("Property `$name` is not defined in gradle.properties")

val SourceSet.kotlin: SourceDirectorySet
    get() = this.extensions.getByName<KotlinSourceSet>("kotlin").kotlin

fun SourceSet.kotlin(action: SourceDirectorySet.() -> Unit) =
    kotlin.action()

fun getLastReleaseNotes(changLogPath: String = "CHANGELOG.md"): String {
    val src = File(project.projectDir, changLogPath).readText()
    val flavour = org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor()
    val parsedTree = org.intellij.markdown.parser.MarkdownParser(flavour).buildMarkdownTreeFromString(src)

    var found = false
    val releaseNotesChildren: MutableList<org.intellij.markdown.ast.ASTNode> = mutableListOf()

    for (child in parsedTree.children) {
        if (child.type == org.intellij.markdown.MarkdownElementTypes.ATX_1) {
            if (found) {
                // collect finished. exit
                break
            }
            if (child.getTextInNode(src).startsWith("# Release notes for v")) {
                releaseNotesChildren.add(child)
                found = true
            }
        } else {
            if (found) {  // collect child related to this release note
                releaseNotesChildren.add(child)
            }
        }
    }

    if (!found) {
        throw Exception("Can not find releases notes in '${changLogPath}'")
    }
    val root = org.intellij.markdown.ast.CompositeASTNode(
        org.intellij.markdown.MarkdownElementTypes.MARKDOWN_FILE,
        releaseNotesChildren
    )
    return org.intellij.markdown.html.HtmlGenerator(src, root, flavour).generateHtml()
}
