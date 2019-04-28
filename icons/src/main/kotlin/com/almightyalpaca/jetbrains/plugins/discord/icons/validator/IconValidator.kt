package com.almightyalpaca.jetbrains.plugins.discord.icons.validator

import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.getLocalIcons
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.local.LocalSource
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun main() = runBlocking {
    val source = LocalSource(Paths.get("../"), retry = false)
    val themes = source.getThemes()

    var violation = false

    for (theme in themes.keys) {
        val icons = getLocalIcons(theme)

        violation = validate(theme, icons)
    }

    if (violation)
        exitProcess(-1)
}

private fun validate(theme: String, icons: Set<String>): Boolean {
    var violation = false

    if (icons.size > 149)
        println("Theme $theme has too many icons: ${icons.size + 1}")

    for (icon in icons) {
        val path = Paths.get("themes/$theme/$icon.png")

        val image = ImageIO.read(Files.newInputStream(path))

        val width = image.width
        val height = image.height

        if (width != 1024 || height != 1024) {
            violation = true

            println("Icon $theme/$icon.png has wrong dimensions: ${width}x$height")
        }
    }

    return violation
}
