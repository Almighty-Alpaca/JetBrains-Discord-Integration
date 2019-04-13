package com.almightyalpaca.jetbrains.plugins.discord.shared.source

import java.awt.image.BufferedImage

interface Asset {
    val id: String
    val theme: Theme

    fun getImage(size: Int?): BufferedImage?
}
