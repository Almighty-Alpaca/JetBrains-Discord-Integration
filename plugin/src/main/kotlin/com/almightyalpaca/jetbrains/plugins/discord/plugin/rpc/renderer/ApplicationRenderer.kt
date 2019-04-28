package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings

class ApplicationRenderer(context: RenderContext) : Renderer(context) {
    override fun RenderContext.forceRender() = forceRender(
        details = settings.applicationDetails,
        detailsCustom = settings.applicationDetailsCustom,
        state = settings.applicationState,
        stateCustom = settings.applicationStateCustom,
        largeIcon = settings.applicationIconLarge,
        largeIconText = settings.applicationIconLargeText,
        smallIcon = settings.applicationIconSmall,
        smallIconText = settings.applicationIconSmallText,
        startTimestamp = settings.applicationTime
    )
}
