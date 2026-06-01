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
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType


val platformType = prop("platformType")
val platformVersion = prop("platformVersion")
val verifierIdeVersion = platformVersion

val psiViewerPluginVersion = prop("psiViewerPluginVersion")
val channel = prop("publishChannel")

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        // needed to extract the last release notes
        classpath("org.jetbrains:markdown:0.2.0")
        // needed for grammar-kit parser generation on newer IntelliJ versions
        classpath("it.unimi.dsi:fastutil:8.5.12")
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
    kotlin("jvm") version "2.3.20"
    id("org.jetbrains.intellij.platform.module") version "2.16.0"
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
        create("grammarKitClasspath")
    }

    dependencies {
        testImplementation("junit", "junit", "4.13.2")
        testImplementation("org.opentest4j", "opentest4j", "1.3.0")
        implementation("com.github.kittinunf.fuel", "fuel", "2.3.1") {
            exclude("org.jetbrains.kotlin")
        }
        testImplementation("org.assertj:assertj-core:3.24.2")
        "grammarKitClasspath"("it.unimi.dsi:fastutil:8.5.12")

        intellijPlatform {
            create(platformType, platformVersion)
            val pluginList = mutableListOf(
                "PsiViewer:$psiViewerPluginVersion",
                "com.redhat.devtools.lsp4ij:0.19.4"
            )
            plugins(pluginList)

            if (platformType == "IU") {
                bundledPlugin("com.intellij.java")
            }
            bundledPlugin("com.intellij.modules.json")
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

    kotlin {
        jvmToolchain(21)
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
                // Use single IDE version on CI to reduce risk of running out of disk space on GHA runner
                if (System.getenv("CI") != null) {
                    create(IntelliJPlatformType.IntellijIdeaUltimate, verifierIdeVersion)
                } else {
                    recommended()
                }
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
            pluginComposedModule(implementation(project(":")))
            pluginComposedModule(implementation(project(":idea")))
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

    val generateRegoLexer = tasks.register<GenerateLexerTask>("generateRegoLexer") {
        sourceFile.set(file("src/main/grammar/RegoLexer.flex"))
        targetOutputDir.set(project.layout.projectDirectory.dir("src/main/gen/org/openpolicyagent/ideaplugin/lang/lexer"))
        purgeOldFiles.set(true)
    }


    val generateRegoParser = tasks.register<GenerateParserTask>("generateRegoParser") {
        sourceFile.set(file("src/main/grammar/Rego.bnf"))
        targetRootOutputDir.set(project.layout.projectDirectory.dir("src/main/gen"))
        pathToParser.set("/org/openpolicyagent/ideaplugin/lang/parser/RegoParser.java")
        pathToPsiRoot.set("/org/openpolicyagent/ideaplugin/lang/psi")
        purgeOldFiles.set(true)
    }

    // fastutil is needed by GrammarKit but must not be in implementation — it conflicts with the
    // platform's bundled version at test runtime, causing NoSuchMethodError in every test.
    tasks.withType<GenerateParserTask> {
        classpath = configurations.compileClasspath.get() + configurations["grammarKitClasspath"]
    }

    tasks.withType<KotlinCompile> {
        dependsOn(
            generateRegoLexer,
            generateRegoParser
        )
        compilerOptions {
            // prevents Kotlin from generating bridge stubs for interface default methods,
            // which the plugin verifier flags as deprecated API overrides
            freeCompilerArgs.add("-jvm-default=no-compatibility")
        }
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
