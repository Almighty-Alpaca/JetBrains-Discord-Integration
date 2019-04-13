package com.almightyalpaca.jetbrains.plugins.discord.plugin.source.bintray

import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.roundToNextPowerOfTwo
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractAsset
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.get
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.getCompletedOrNull
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO

class BintrayAsset(id: String, private val iconSet: BintrayIconSet) : AbstractAsset(id, iconSet.theme) {
    override fun getImage(size: Int?): BufferedImage? = getIconURL(size)?.get { stream ->
        ImageIO.read(stream)
    }

    private fun getIconURL(size: Int?): URL? {
        val applicationId = iconSet.applicationId
        val iconId = iconSet.iconIdJob.getCompletedOrNull()?.get(id)

        return if (applicationId != null && iconId != null) {
            when (size) {
                null -> URL("https://cdn.discordapp.com/app-assets/$applicationId/$iconId.png")
                else -> URL("https://cdn.discordapp.com/app-assets/$applicationId/$iconId.png?size=${size.roundToNextPowerOfTwo().coerceIn(16..4096)}")
            }
        } else {
            null
        }
    }
}
