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


val ideaVersion = prop("ideaVersion")
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
    id("org.jetbrains.intellij") version "0.4.16"
    id("org.jetbrains.grammarkit") version "2020.1"
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
        version = ideaVersion

        tasks {
            withType<org.jetbrains.intellij.tasks.PatchPluginXmlTask> {
                sinceBuild(prop("sinceBuild"))
                untilBuild(prop("untilBuild"))
            }
        }
    }

    sourceSets {
        main {
            java.srcDirs("src/main/gen")
            kotlin.srcDirs("src/main/kotlin")
            resources.srcDirs("src/main/resources")
        }
        test {
            kotlin.srcDirs("src/test/kotlin")
            resources.srcDirs("src/test/resources")
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


// TODO MORE EXPLICIT --- Special module with run, build and publish tasks
project(":plugin"){
    version = "1.0.0-SNAPSHOT" // TODO compute version, like rust plugin include the chanel suffix in version
    intellij {
        pluginName = "opa-idea-plugin"
        val plugins = mutableListOf(
            "PsiViewer:$psiViewerPluginVersion",
            "java" // TODO check that
        )
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
        //TODO NEEDED ?
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
    intellij {
        version = ideaVersion
        setPlugins("java")
    }
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