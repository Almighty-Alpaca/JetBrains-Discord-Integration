package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings

class ProjectRenderer(context: RenderContext) : Renderer(context) {
    override fun RenderContext.forceRender() = forceRender(
        details = settings.projectDetails,
        detailsCustom = settings.projectDetailsCustom,
        state = settings.projectState,
        stateCustom = settings.projectStateCustom,
        largeIcon = settings.projectIconLarge,
        largeIconText = settings.projectIconLargeText,
        smallIcon = settings.projectIconSmall,
        smallIconText = settings.projectIconSmallText,
        startTimestamp = settings.projectTime
    )
}
