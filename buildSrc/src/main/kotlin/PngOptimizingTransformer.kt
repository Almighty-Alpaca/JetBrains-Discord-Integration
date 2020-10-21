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

import com.github.jengelman.gradle.plugins.shadow.transformers.CacheableTransformer
import com.github.jengelman.gradle.plugins.shadow.transformers.Transformer
import com.github.jengelman.gradle.plugins.shadow.transformers.TransformerContext
import com.googlecode.pngtastic.core.PngImage
import com.googlecode.pngtastic.core.PngOptimizer
import net.openhft.hashing.LongHashFunction
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.gradle.api.file.FileTreeElement
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
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

@Suppress("FunctionName")
fun Project.PngOptimizingTransformer(size: Int, vararg includePaths: Regex): Transformer =
    PngOptimizingTransformer(size, buildDir.toPath().resolve("cache/icons"), *includePaths)

@CacheableTransformer
class PngOptimizingTransformer(
    @Input
    private val size: Int,
    @InputDirectory
    private val cacheDir: Path,
    @Input
    private vararg val includePaths: Regex
) : Transformer {
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

        val cacheFile = cacheDir.resolve("$hash-$size")

        if (!Files.exists(cacheFile)) {
            val image: BufferedImage = ImageIO.read(ByteArrayInputStream(data)) ?: return

            val scaledImage: RenderedImage = image.getScaledInstance(size, size, Image.SCALE_SMOOTH).getRenderedImage()
            val imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream)

            val writer = ImageIO.getImageWritersByFormatName("png").next()
            val param = writer.defaultWriteParam

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
