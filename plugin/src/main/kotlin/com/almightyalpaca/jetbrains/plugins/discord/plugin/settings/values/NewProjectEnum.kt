package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.ToolTipProvider

typealias NewProjectShowValue = SimpleValue<NewProjectShow>

enum class NewProjectShow(val description: String, override val toolTip: String? = null):ToolTipProvider {
    SHOW("Show"),
    HIDE("Hide"),
    ASK("Ask every time");

    override fun toString() = description
}
