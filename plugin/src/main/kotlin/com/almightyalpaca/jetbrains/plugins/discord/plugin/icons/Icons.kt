package com.almightyalpaca.jetbrains.plugins.discord.plugin.icons

import com.intellij.openapi.util.IconLoader

object Icons {
    val DISCORD_BLURPLE = "/discord/images/logo/blurple.png"()
    val DISCORD_WHITE = "/discord/images/logo/white.png"()

    private operator fun String.invoke() = IconLoader.getIcon(this)
}
