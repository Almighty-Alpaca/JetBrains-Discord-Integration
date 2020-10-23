/*
 * Copyright 2017-2020 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.io.FilenameUtils
import org.glavo.javah.ClassName
import org.glavo.javah.ClassPath
import org.glavo.javah.JniHeaderGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.*
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.internal.file.Deleter
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject
import kotlin.streams.toList

class GenerateJniHeadersPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val generateJniHeaders by target.tasks.registering {
            group = "build"
            description = "Generates all JNI headers"
        }

        target.pluginManager.withPlugin("java") {
            val generateJniHeadersJava by target.tasks.registering(GenerateJniHeadersTask::class) {
                description = "Generates JNI headers for Java classes"

                val compileJavaTask = target.tasks.named<JavaCompile>("compileJava")

                dependsOn(compileJavaTask)

                @Suppress("UnstableApiUsage")
                classDirectory.set(compileJavaTask.flatMap { it.destinationDirectory })
            }

            generateJniHeaders {
                dependsOn(generateJniHeadersJava)
            }
        }

        target.pluginManager.withPlugin("java") {
            val generateJniHeadersKotlin by target.tasks.registering(GenerateJniHeadersTask::class) {
                description = "Generates JNI headers for Kotlin classes"

                val compileKotlinTask = target.tasks.named<KotlinCompile>("compileKotlin")

                dependsOn(compileKotlinTask)

                @Suppress("UnstableApiUsage")
                classDirectory.set(compileKotlinTask.flatMap { it.destinationDirectory })
            }

            generateJniHeaders {
                dependsOn(generateJniHeadersKotlin)
            }
        }
    }
}

@CacheableTask
open class GenerateJniHeadersTask : DefaultTask() {
    @SkipWhenEmpty
    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    val classDirectory: DirectoryProperty = project.objects.directoryProperty()

    @OutputDirectory
    val destinationDirectory: DirectoryProperty = project.objects.directoryProperty().apply { set(project.layout.buildDirectory.dir("generated/headers")) }

    protected open val deleter: Deleter
        @Inject get() = throw UnsupportedOperationException("Decorator injects implementation")

    @TaskAction
    private fun generate() {
        val destinationPath = destinationDirectory.get().asFile.toPath()

        deleter.deleteRecursively(destinationPath.toFile(), false)

        val classesPath = classDirectory.get().asFile.toPath()

        Files.createDirectories(classesPath)

        val kotlinClassNames = Files.walk(classesPath)
            .filter { Files.isRegularFile(it) }
            .map(classesPath::relativize)
            .map(Path::toString)
            .map(FilenameUtils::separatorsToUnix)
            .map { it.replace('/', '.') }
            .filter { it.endsWith(".class") }
            .map { it.removeSuffix(".class") }
            .toList()

        val searchPaths = setOf(
            ClassPath(classesPath)
        )

        val generator = JniHeaderGenerator(destinationPath, searchPaths)

        for (className in kotlinClassNames) {
            generator.generate(ClassName.of(className))
        }
    }
}
