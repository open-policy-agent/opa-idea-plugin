/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

import org.gradle.api.internal.HasConvention
import org.intellij.markdown.ast.getTextInNode
import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.intellij.tasks.RunIdeTask
import org.jetbrains.intellij.tasks.PublishPluginTask
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


val baseIDE = prop("baseIDE")
val ideaVersion = prop("ideaVersion")
val pycharmCommunityVersion = prop("pycharmCommunityVersion")
val baseVersion = when (baseIDE) {
    "idea" -> ideaVersion
    "pycharmCommunity" -> pycharmCommunityVersion
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
    kotlin("jvm") version "1.7.21"
    id("org.jetbrains.intellij") version "1.11.0"
    id("org.jetbrains.grammarkit") version "2021.2.2"
}

allprojects {
    apply {
        plugin("idea")
        plugin("kotlin")
        plugin("org.jetbrains.grammarkit")
        plugin("org.jetbrains.intellij")

    }

    repositories {
        mavenCentral()
        maven("https://cache-redirector.jetbrains.com/intellij-dependencies")
    }

    configurations {
        all {
            resolutionStrategy.sortArtifacts(ResolutionStrategy.SortOrder.DEPENDENCY_FIRST)
        }
    }

    dependencies {
        implementation("com.github.kittinunf.fuel", "fuel", "2.3.1") {
            exclude("org.jetbrains.kotlin")
        }
        testImplementation("org.assertj:assertj-core:3.16.1")
    }

    idea {
        module {
            generatedSourceDirs.add(file("src/main/gen"))
        }
    }

    intellij {
        version.set(baseVersion)
        sandboxDir.set("$buildDir/$baseIDE-sandbox-$baseVersion")
    }

    // Set the JVM language level used to build project. Use Java 11 for 2020.3+, and Java 17 for 2022.2+.
    kotlin {
        jvmToolchain(11)
    }

    sourceSets {
        main {
            java.srcDirs("src/main/gen")
        }
    }

    tasks {
        // There is a bug in gradle and tests are not detected. This is a workaround until gradle 7.5 is released.
        // More information at https://youtrack.jetbrains.com/issue/IDEA-278926#focus=Comments-27-5561012.0-0 and
        // https://github.com/gradle/gradle/pull/20123
        val test by getting(Test::class) {
            setScanForTestClasses(false)
            // Only run tests from classes that end with "Test"
            include("**/*Test.class")
        }

        withType<org.jetbrains.intellij.tasks.PatchPluginXmlTask> {
            sinceBuild.set(prop("sinceBuild"))
            untilBuild.set(prop("untilBuild"))
            changeNotes.set(provider { getLastReleaseNotes() })
        }

        withType<RunIdeTask> {
            jvmArgs("--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED")
        }

        buildSearchableOptions {
            // buildSearchableOptions task doesn't make sense for non-root subprojects
            enabled = false
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
    version = "$pluginVersion$channelSuffix"
    intellij {
        pluginName.set("opa-idea-plugin")
        val pluginList = mutableListOf(
            "PsiViewer:$psiViewerPluginVersion"
        )
        if (baseIDE == "idea") {
            pluginList += listOf(
                "java"
            )
        }
        plugins.set(pluginList)
    }

    dependencies {
        implementation(project(":"))
        implementation(project(":idea"))
    }

    tasks {
        buildPlugin {
            // Set proper name for final plugin zip.
            // Otherwise, base name is the same as gradle module name
            archiveBaseName.set("opa-idea-plugin")
        }
        withType<RunIdeTask> {
            jvmArgs("--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED")
        }
        withType<PublishPluginTask> {
            token.set(prop("publishToken"))
            channels.set(listOf(channel))
        }
        buildSearchableOptions {
            // buildSearchableOptions task doesn't make sense for non-root subprojects
            enabled = prop("enableBuildSearchableOptions").toBoolean()
        }
    }
}

project(":") {
    val testOutput = configurations.create("testOutput")

    dependencies {
        testOutput(sourceSets.getByName("test").output.classesDirs)
    }

    val generateRegoLexer = task<GenerateLexerTask>("generateRegoLexer") {
        source.set("src/main/grammar/RegoLexer.flex")
        targetDir.set("src/main/gen/org/openpolicyagent/ideaplugin/lang/lexer")
        targetClass.set("_RegoLexer")
        purgeOldFiles.set(true)
    }


    val generateRegoParser = task<GenerateParserTask>("generateRegoParser") {
        source.set("src/main/grammar/Rego.bnf")
        targetRoot.set("src/main/gen")
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
    task("resolveDependencies") {
        doLast {
            rootProject.allprojects
                .map { it.configurations }
                .flatMap { it.filter { c -> c.isCanBeResolved } }
                .forEach { it.resolve() }
        }
    }
}

project(":idea") {
    dependencies {
        implementation(project(":"))
        testImplementation(project(":", "testOutput"))
    }
}

fun prop(name: String): String =
    extra.properties[name] as? String
        ?: error("Property `$name` is not defined in gradle.properties")

val SourceSet.kotlin: SourceDirectorySet
    get() =
        (this as HasConvention)
            .convention
            .getPlugin(KotlinSourceSet::class.java)
            .kotlin


fun SourceSet.kotlin(action: SourceDirectorySet.() -> Unit) =
    kotlin.action()

fun getLastReleaseNotes(changLogPath: String = "CHANGELOG.md"): String {
    val src = File(changLogPath).readText()
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
