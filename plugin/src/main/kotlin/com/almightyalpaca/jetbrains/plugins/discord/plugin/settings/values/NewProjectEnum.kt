package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue

typealias NewProjectShowValue = SimpleValue<NewProjectShow>

enum class NewProjectShow(val description: String) {
    SHOW("Show"),
    HIDE("Hide"),
    ASK("Ask every time");

    override fun toString() = description
}
