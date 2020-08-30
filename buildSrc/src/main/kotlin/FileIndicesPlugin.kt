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

import com.google.common.collect.Multimaps
import com.google.common.collect.SetMultimap
import org.apache.commons.io.FilenameUtils
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.gradle.internal.file.Deleter
import org.gradle.kotlin.dsl.dir
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject

private val Project.sourceSets
    get() = extensions.getByType(SourceSetContainer::class)

class FileIndicesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val task = target.tasks.register("generateFileIndices", GenerateFileIndices::class)

        target.sourceSets.named("main") {
            output.dir(task.map { it.outputDir }, "builtBy" to task)
        }

    }
}

@CacheableTask
open class GenerateFileIndices : DefaultTask() {
    private val resourceDirs: Provider<SourceDirectorySet> =
        project
            .extensions
            .getByType(SourceSetContainer::class)
            .named("main")
            .map { it.resources }

    @InputFiles
    @PathSensitive(PathSensitivity.ABSOLUTE)
    @Suppress("unused")
    val resourceDirFileTree: Provider<FileTree> = resourceDirs.map { project.files(resourceDirs).asFileTree }

    @OutputDirectory
    var outputDir: Path = project.buildDir.resolve("generated/resources/main/fileIndices").toPath()

    @Input
    var indexFileName = "index"

    @Input
    val paths = mutableListOf<String>()

    open val deleter: Deleter
        @Inject get() = throw UnsupportedOperationException("Decorator injects implementation")

    @TaskAction
    private fun generate() {
        deleter.deleteRecursively(outputDir.toFile(), false)

        val fileMap: SetMultimap<String, String> = Multimaps.newSetMultimap(HashMap()) { HashSet<String>() }

        for (resourceDir in resourceDirs.get().srcDirs) {
            val resourcePath = resourceDir.toPath()

            Files.walk(resourcePath)
                .filter { Files.isRegularFile(it) }
                .map { resourcePath.relativize(it) }
                .map { it.toString() }
                .map { FilenameUtils.separatorsToUnix(it) }
                .filter { filePath -> paths.any { filePath.startsWith(it) } }
                .forEach { filePath ->
                    fileMap.put(FilenameUtils.getPathNoEndSeparator(filePath), FilenameUtils.getName(filePath))
                }
        }

        for ((path, files) in fileMap.asMap()) {
            val indexFile = outputDir.resolve("$path/$indexFileName")
            Files.createDirectories(indexFile.parent)
            Files.newBufferedWriter(indexFile).append(files.joinToString(separator = "\n") { it }).close()
        }
    }
}
