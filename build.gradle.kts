/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

import org.gradle.api.JavaVersion.VERSION_1_8
import org.gradle.api.internal.HasConvention
import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.intellij.tasks.RunIdeTask
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


val ideaVersion = prop("ideaVersion")
val psiViewerPluginVersion = prop("psiViewerPluginVersion")

plugins {
    idea
    kotlin("jvm") version "1.3.50"
    id("org.jetbrains.intellij") version "0.4.16"
    id("org.jetbrains.grammarkit") version "2020.1"
    id("de.undercouch.download") version "3.4.3"
    id("net.saliman.properties") version "1.4.6"

}


apply {
    plugin("idea")
    plugin("kotlin")
    plugin("org.jetbrains.grammarkit")
    plugin("org.jetbrains.intellij")

}

group = "com.github.vgramer"
version = "1.0.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.assertj:assertj-core:3.16.1")
}

idea {
    module {
        generatedSourceDirs.add(file("src/main/gen"))

        // https://github.com/gradle/kotlin-dsl/issues/537/
        excludeDirs = excludeDirs + file("testData") + file("deps")
    }
}

intellij {
    version = ideaVersion
    val plugins = mutableListOf(
        "PsiViewer:$psiViewerPluginVersion"
    )

    tasks{
        withType<org.jetbrains.intellij.tasks.PatchPluginXmlTask> {
            sinceBuild(prop("sinceBuild"))
            untilBuild(prop("untilBuild"))
        }
    }
    setPlugins(*plugins.toTypedArray())
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

val generateRegoLexer = task<GenerateLexer>("generateRegoLexer") {
    source = "src/main/grammar/RegoLexer.flex"
    targetDir = "src/main/gen/com/github/vgramer/opaplugin/lang/lexer"
    targetClass = "_RegoLexer"
    purgeOldFiles = true
}


val generateRegoParser = task<GenerateParser>("generateRegoParser") {
    source = "src/main/grammar/Rego.bnf"
    targetRoot = "src/main/gen"
    pathToParser =  "/com/github/vgramer/opaplugin/lang/parser/RegoParser.java"
    pathToPsiRoot = "/com/github/vgramer/opaplugin/lang/psi"
    purgeOldFiles = true
}

tasks.withType<KotlinCompile> {
    dependsOn(
        generateRegoLexer,
        generateRegoParser
    )
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