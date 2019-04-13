package com.almightyalpaca.jetbrains.plugins.discord.shared.source.local

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Theme
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractAsset
import java.awt.image.BufferedImage
import java.nio.file.Files
import javax.imageio.ImageIO

class LocalAsset(private val source: LocalSource, id: String, theme: Theme, private val applicationCode: String) : AbstractAsset(id, theme) {
    override fun getImage(size: Int?): BufferedImage? = when (id) {
        "application" -> ImageIO.read(Files.newInputStream(source.pathApplications.resolve("$applicationCode.png")))
        else -> ImageIO.read(Files.newInputStream(source.pathThemes.resolve("${theme.id}/$id.png")))
    }
}
