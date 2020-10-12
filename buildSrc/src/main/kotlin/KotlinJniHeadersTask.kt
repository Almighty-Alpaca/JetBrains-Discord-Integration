import org.glavo.javah.JavahTask
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.internal.file.Deleter
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject
import kotlin.streams.toList

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

@CacheableTask
open class KotlinJniHeadersTask : DefaultTask() {
    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    val kotlinClassesDir: Path = project.buildDir.resolve("classes/kotlin/main").toPath()

    @OutputDirectory
    var outputDir: Path = project.buildDir.resolve("generated/jni-headers").toPath()

    open val deleter: Deleter
        @Inject get() = throw UnsupportedOperationException("Decorator injects implementation")

    init {
        dependsOn(project.tasks.withType<KotlinCompile>())
    }

    @TaskAction
    private fun generate() {
        deleter.deleteRecursively(outputDir.toFile(), false)

        val kotlinClassesNames = Files.walk(kotlinClassesDir)
            .filter { Files.isRegularFile(it) }
            .map(kotlinClassesDir::relativize)
            .map(Path::toString)
            .map { it.replace('\\', '.') }
            .filter { it.endsWith(".class") }
            .map { it.removeSuffix(".class") }
            .toList()

        val task = JavahTask()
        task.outputDir = project.buildDir.resolve("generated/jni-headers").toPath()
        task.addClassPath(kotlinClassesDir)
        task.addClasses(kotlinClassesNames)
        task.run()
    }
}
