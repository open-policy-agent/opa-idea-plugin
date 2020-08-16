/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

import org.gradle.api.JavaVersion.VERSION_1_8
import org.gradle.api.internal.HasConvention
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

idea {
    module {
        // https://github.com/gradle/kotlin-dsl/issues/537/
        excludeDirs = excludeDirs + file("testData") + file("deps")
    }
}

plugins {
    idea
    kotlin("jvm") version "1.3.50"
    id("org.jetbrains.intellij") version "0.4.21"
    id("org.jetbrains.grammarkit") version "2020.2.1"
    id("de.undercouch.download") version "3.4.3"
    id("net.saliman.properties") version "1.4.6"
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
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("com.github.kittinunf.fuel", "fuel", "2.2.3")
        testImplementation("org.assertj:assertj-core:3.16.1")
    }

    idea {
        module {
            generatedSourceDirs.add(file("src/main/gen"))
        }
    }

    intellij {
        version = baseVersion
        // location of IDE distributions, we customize it to easily run plugin verifier
        ideaDependencyCachePath = dependencyCachePath
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
        }

        withType<RunIdeTask> {
            jvmArgs("--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED")
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

val Project.dependencyCachePath get(): String {
    val cachePath = file("${rootProject.projectDir}/deps")
    // If cache path doesn't exist, we need to create it manually
    // because otherwise gradle-intellij-plugin will ignore it
    if (!cachePath.exists()) {
        cachePath.mkdirs()
    }
    return cachePath.absolutePath
}

val channelSuffix = if (channel.isBlank() || channel == "stable") "" else "-$channel"
val versionSuffix = "-$platformVersion$channelSuffix"
val majorVersion = prop("majorVersion")
val patchVersion = prop("patchVersion").toInt()
val buildNumber =  System.getenv("GITHUB_RUN_ID" ) ?: "SNAPSHOT"

// module to build/run/publish opa-ida-plugin plugin
project(":plugin"){
    version = "$majorVersion.$patchVersion.$buildNumber$versionSuffix"
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