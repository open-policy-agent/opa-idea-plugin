/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

import org.gradle.api.JavaVersion.VERSION_1_8
import org.gradle.api.internal.HasConvention
import org.intellij.markdown.ast.getTextInNode
import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.intellij.tasks.RunIdeTask
import org.jetbrains.intellij.tasks.PublishTask
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


val platformVersion = prop("platformVersion").toInt()
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
        excludeDirs = excludeDirs + file("testData") + file("deps")
    }
}

plugins {
    idea
    kotlin("jvm") version "1.4.32"
    id("org.jetbrains.intellij") version "0.7.2"
    id("org.jetbrains.grammarkit") version "2021.1.2"
    id("net.saliman.properties") version "1.5.1"
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
        jcenter()
        maven("https://cache-redirector.jetbrains.com/intellij-dependencies")
    }

    configurations {
        all {
            resolutionStrategy.sortArtifacts(ResolutionStrategy.SortOrder.DEPENDENCY_FIRST)
        }
    }

    dependencies {
        implementation("com.github.kittinunf.fuel", "fuel", "2.2.3"){
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
        version = baseVersion
        sandboxDirectory = "$buildDir/$baseIDE-sandbox-$platformVersion"
    }

    sourceSets {
        main {
            java.srcDirs("src/main/gen")
            kotlin.srcDirs("src/$platformVersion/main/kotlin")
            resources.srcDirs("src/$platformVersion/main/resources")
        }
        test {
            kotlin.srcDirs("src/$platformVersion/test/kotlin")
            resources.srcDirs("src/$platformVersion/test/resources")
        }
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = VERSION_1_8
        targetCompatibility = VERSION_1_8
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
                languageVersion = "1.3"
                apiVersion = "1.3"
                freeCompilerArgs = listOf("-Xjvm-default=enable")
            }
        }

        withType<org.jetbrains.intellij.tasks.PatchPluginXmlTask> {
            sinceBuild(prop("sinceBuild"))
            untilBuild(prop("untilBuild"))
            changeNotes(getLastReleaseNotes())
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
val versionSuffix = "-$platformVersion$channelSuffix"
val pluginVersion = prop("pluginVersion")


// module to build/run/publish opa-ida-plugin plugin
project(":plugin"){
    version = "$pluginVersion$versionSuffix"
    intellij {
        pluginName = "opa-idea-plugin"
        val plugins = mutableListOf(
            "PsiViewer:$psiViewerPluginVersion"
        )
        if (baseIDE == "idea") {
            plugins += listOf(
                "java"
            )
        }
        setPlugins(*plugins.toTypedArray())
    }

    dependencies{
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
        withType<PublishTask> {
            token(prop("publishToken"))
            channels(channel)
        }
        runPluginVerifier {
            ideVersions(prop("pluginVerifierIdeVersions"))
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

    val generateRegoLexer = task<GenerateLexer>("generateRegoLexer") {
        source = "src/main/grammar/RegoLexer.flex"
        targetDir = "src/main/gen/org/openpolicyagent/ideaplugin/lang/lexer"
        targetClass = "_RegoLexer"
        purgeOldFiles = true
    }


    val generateRegoParser = task<GenerateParser>("generateRegoParser") {
        source = "src/main/grammar/Rego.bnf"
        targetRoot = "src/main/gen"
        pathToParser = "/org/openpolicyagent/ideaplugin/lang/parser/RegoParser.java"
        pathToPsiRoot = "/org/openpolicyagent/ideaplugin/lang/psi"
        purgeOldFiles = true
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
                .flatMap { listOf(it.compile, it.testCompile) }
                .forEach { it.get().resolve() }
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