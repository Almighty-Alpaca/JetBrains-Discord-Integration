package com.almightyalpaca.jetbrains.plugins.discord.plugin.icons

import com.intellij.openapi.util.IconLoader

object Icons {
    val DISCORD_BLURPLE = "/images/discord/logo/blurple.png"()
    val DISCORD_WHITE = "/images/discord/logo/white.png"()

    private operator fun String.invoke() = IconLoader.getIcon(this)
}
