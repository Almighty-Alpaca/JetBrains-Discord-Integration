package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings

class FileRenderer(context: RenderContext) : Renderer(context) {
    override fun RenderContext.forceRender() = forceRender(
            details = settings.fileDetails,
            detailsCustom = settings.fileDetailsCustom,
            state = settings.fileState,
            stateCustom = settings.fileStateCustom,
            largeIcon = settings.fileIconLarge,
            largeIconText = settings.fileIconLargeText,
            smallIcon = settings.fileIconSmall,
            smallIconText = settings.fileIconSmallText,
            startTimestamp = settings.fileTime)
}
