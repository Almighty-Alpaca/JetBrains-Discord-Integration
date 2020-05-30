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

@file:Suppress("SuspiciousCollectionReassignment")

import com.github.jengelman.gradle.plugins.shadow.relocation.SimpleRelocator
import com.github.jengelman.gradle.plugins.shadow.transformers.Transformer
import com.github.jengelman.gradle.plugins.shadow.transformers.TransformerContext
import com.googlecode.pngtastic.core.PngImage
import com.googlecode.pngtastic.core.PngOptimizer
import net.openhft.hashing.LongHashFunction
import org.apache.commons.io.IOUtils
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jsoup.Jsoup
import shadow.org.apache.tools.zip.ZipEntry
import shadow.org.apache.tools.zip.ZipOutputStream
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij")
    id("com.github.johnrengelman.shadow")
    id("com.palantir.baseline-exact-dependencies")
}

val github = "https://github.com/Almighty-Alpaca/JetBrains-Discord-Integration"

dependencies {
    val versionJackson: String by project
    val versionOkHttp: String by project
    val versionRpc: String by project
    val versionCommonsIo: String by project

    implementation(project(":icons")) {
        exclude(group = "org.slf4j", module = "slf4j-api")
        excludeKotlinLibraries()
    }

    implementation(project(":analytics:interface")) {
        excludeKotlinLibraries()
    }

    implementation(group = "club.minnced", name = "java-discord-rpc", version = versionRpc)

    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = versionOkHttp) {
        excludeKotlinLibraries()
    }

    implementation(group = "commons-io", name = "commons-io", version = versionCommonsIo)

    implementation(group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml", version = versionJackson)
}

fun ModuleDependency.excludeKotlinLibraries() {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-common")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-test")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-test-common")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-jdk8")
}

val isCI by lazy { System.getenv("CI") != null }

intellij {
    // https://www.jetbrains.com/intellij-repository/releases
    // https://www.jetbrains.com/intellij-repository/snapshots
    version = "2020.1.1"

    downloadSources = !isCI

    updateSinceUntilBuild = false

    sandboxDirectory = "${project.rootDir.absolutePath}/.sandbox"

    instrumentCode = false

    setPlugins("git4idea")

    // For testing with a custom theme
    // setPlugins("git4idea", "com.chrisrm.idea.MaterialThemeUI:3.10.0")
}

tasks {
    checkUnusedDependencies {
        ignore("com.jetbrains", "ideaIU")
    }

    checkImplicitDependencies {
        ignore("org.jetbrains", "annotations")
    }

    patchPluginXml {
        changeNotes(readInfoFile(project.file("CHANGELOG.md")))
        pluginDescription(readInfoFile(project.file("DESCRIPTION.md")))
    }

    runIde {
        // use local icons
        // environment["com.almightyalpaca.jetbrains.plugins.discord.plugin.source"] = "local:${project(":icons").parent!!.projectDir.absolutePath}"

        // use icons from specific bintray repo
        // environment["com.almightyalpaca.jetbrains.plugins.discord.plugin.source"] = "bintray:almightyalpaca/JetBrains-Discord-Integration/Icons"

        // use classpath icons
        // environment["com.almightyalpaca.jetbrains.plugins.discord.plugin.source"] = "classpath:discord"
    }

    publishPlugin {
        if (project.extra.has("JETBRAINS_TOKEN")) {
            token(project.extra["JETBRAINS_TOKEN"])
        } else {
            enabled = false
        }

        if (!(version as String).matches(Regex("""\d+\.\d+\.\d+"""))) {
            channels("eap")
        } else {
            channels("default", "eap")
        }
    }

    prepareSandbox task@{
        setLibrariesToIgnore(*configurations.filter { it.isCanBeResolved }.toTypedArray())

        dependsOn(shadowJar)

        pluginJar(shadowJar.get().archiveFile)
    }

    build {
        dependsOn(buildPlugin)
    }

    check {
        dependsOn(verifyPlugin)
    }

    shadowJar task@{
        fun prefix(pkg: String, configure: Action<SimpleRelocator>? = null) =
            relocate(pkg, "${rootProject.group}.dependencies.$pkg", configure)

        mergeServiceFiles()

        prefix("club.minnced.discord.rpc")
        prefix("com.fasterxml.jackson.annotation")
        prefix("com.fasterxml.jackson.core")
        prefix("com.fasterxml.jackson.databind")
        prefix("com.fasterxml.jackson.dataformat.yaml")
        prefix("com.jagrosh.discordipc")
        prefix("javassist")
        prefix("okhttp3")
        prefix("okio")
        prefix("org.apache.commons.io")
        prefix("org.reflections")
        prefix("org.yaml.snakeyaml")

        val iconPaths = listOf(
            Regex("""/?discord/applications/.*\.png"""),
            Regex("""/?discord/themes/.*\.png""")
        )

        transform(PngOptimizeTransformer(128, 0.9F, iconPaths))
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
        }
    }

    withType<AbstractArchiveTask> {
        archiveBaseName.set("${rootProject.name}-${project.name.capitalize()}")
    }

    processResources {
        filesMatching("/discord/changes.html") {
            val document = Jsoup.parse(readInfoFile(project.file("CHANGELOG.md")))
            val body = document.getElementsByTag("body")[0]
            val list = body.getElementsByTag("ul")[0]

            expand("changes" to list.toString())
        }
    }

    create("printChangelog") {
        group = "markdown"

        doLast {
            println(readInfoFile(project.file("CHANGELOG.md")))
        }
    }

    create("printDescription") {
        group = "markdown"

        doLast {
            println(readInfoFile(project.file("DESCRIPTION.md")))
        }
    }
}

fun readInfoFile(file: File): String {
    operator fun MatchResult.get(i: Int) = groupValues[i]

    return file.readText()
        // Remove unnecessary whitespace
        .trim()

        // Replace headlines
        .replace(Regex("(\\r?\\n|^)##(.*)(\\r?\\n|\$)")) { match -> "${match[1]}<b>${match[2]}</b>${match[3]}" }

        // Replace issue links
        .replace(Regex("\\[([^\\[]+)\\]\\(([^\\)]+)\\)")) { match -> "<a href=\"${match[2]}\">${match[1]}</a>" }
        .replace(Regex("\\(#([0-9]+)\\)")) { match -> "(<a href=\"$github/issues/${match[1]}\">#${match[1]}</a>)" }

        // Replace inner lists
        .replace(Regex("\r?\n  - (.*)")) { match -> "<li>${match[1]}</li>" }
        .replace(Regex("((?:<li>.*</li>)+)")) { match -> "<ul>${match[1]}</ul>" }

        // Replace lists
        .replace(Regex("\r?\n- (.*)")) { match -> "<li>${match[1]}</li>" }
        .replace(Regex("((?:<li>.*</li>)+)")) { match -> "<ul>${match[1]}</ul>" }
        .replace(Regex("\\s*<li>\\s*"), "<li>")
        .replace(Regex("\\s*</li>\\s*"), "</li>")
        .replace(Regex("\\s*<ul>\\s*"), "<ul>")
        .replace(Regex("\\s*</ul>\\s*"), "</ul>")

        // Replace newlines
        .replace("\n", "<br>")
}

fun Project.PngOptimizeTransformer(size: Int, quality: Float, includePaths: List<Regex>) =
    PngOptimizeTransformer(size, quality, includePaths, this.buildDir.toPath().resolve("cache/icons"))

class PngOptimizeTransformer(private val size: Int, private val quality: Float, private val includePaths: List<Regex>, private val cacheDir: Path) : Transformer {
    private val files = mutableMapOf<String, Path>()

    private val cacheFileTemp: Path = cacheDir.resolve("new")

    override fun canTransformResource(element: FileTreeElement): Boolean {
        val path = element.relativePath.pathString
        return includePaths.any { it.matches(path) }
    }

    override fun hasTransformedResource(): Boolean = files.isNotEmpty()

    private val byteArrayOutputStream = ByteArrayOutputStream()

    override fun transform(context: TransformerContext) {
        val data = IOUtils.toByteArray(context.`is`)

        @Suppress("EXPERIMENTAL_API_USAGE")
        val hash = LongHashFunction.xx().hashBytes(data).toULong().toString()

        val cacheFile = cacheDir.resolve(hash)

        if (!Files.exists(cacheFile)) {
            val image: BufferedImage = ImageIO.read(ByteArrayInputStream(data))

            val scaledImage: RenderedImage = image.getScaledInstance(size, size, Image.SCALE_SMOOTH).getRenderedImage()
            val imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream)

            val writer = ImageIO.getImageWritersByFormatName("png").next()
            val param = writer.defaultWriteParam
            param.compressionMode = ImageWriteParam.MODE_EXPLICIT
            param.compressionQuality = quality

            writer.output = imageOutputStream
            writer.write(null, IIOImage(scaledImage, null, null), param)

            val pngImage = PngImage(byteArrayOutputStream.toByteArray())
            val optimizer = PngOptimizer()
            val optimizedPngImage: PngImage = optimizer.optimize(pngImage)

            byteArrayOutputStream.reset()

            Files.createDirectories(cacheDir)

            Files.newOutputStream(cacheFileTemp, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use {
                optimizedPngImage.writeDataOutputStream(it)
            }

            Files.move(cacheFileTemp, cacheFile, StandardCopyOption.REPLACE_EXISTING)
        }

        files[context.path] = cacheFile
    }

    override fun modifyOutputStream(os: ZipOutputStream, preserveFileTimestamps: Boolean) {
        for ((path, cachePath) in files) {
            val entry = ZipEntry(path)
            entry.time = TransformerContext.getEntryTimestamp(preserveFileTimestamps, entry.time)
            os.putNextEntry(entry)
            IOUtils.copy(Files.newInputStream(cachePath), os)
        }
    }

    private fun Image.getRenderedImage(): RenderedImage {
        if (this is RenderedImage) return this

        val image = BufferedImage(getWidth(null), getHeight(null), BufferedImage.TYPE_INT_ARGB)

        // Draw the image on to the buffered image
        val graphics = image.createGraphics()
        graphics.drawImage(this, 0, 0, null)
        graphics.dispose()

        // Return the buffered image
        return image
    }
}
