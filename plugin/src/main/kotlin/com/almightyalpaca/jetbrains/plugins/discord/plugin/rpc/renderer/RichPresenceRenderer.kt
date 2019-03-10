package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer

import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logger
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresenceData
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.richPresence

class RichPresenceRenderer(context: RichPresenceRenderContext) : RichPresenceRenderContext by context {
    override fun render(): RichPresenceData {
        Logger.Level.TRACE { "RichPresenceRenderer#render($application)" }

        val presence = richPresence {
            if (project == null) {
                setLargeImage("application", application.version)
            } else {
                setDetails("Working on " + project.name)
                setStartTimestamp(project.openedAt)

                if (file == null) {
                    setLargeImage("application", application.version)
                } else {
                    setState(
                        when (file.readOnly) {
                            true -> "Reading ${file.name}"
                            false -> "Editing ${file.name}"
                        }
                    )

                    val icon = file.icon
                    setLargeImage(icon.asset, icon.name)
                    setSmallImage("application", application.version)
                }
            }
        }

        return RichPresenceData(icons.appId, presence)
    }

    companion object : Logging()
}
