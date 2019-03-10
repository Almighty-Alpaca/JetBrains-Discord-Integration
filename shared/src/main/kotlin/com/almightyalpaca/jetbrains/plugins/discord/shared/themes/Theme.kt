package com.almightyalpaca.jetbrains.plugins.discord.shared.themes

import com.almightyalpaca.jetbrains.plugins.discord.shared.themes.icons.IconSet
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.lazyMap

class Theme(val id: String, val name: String, val description: String, val applicationIds: Map<String, Long>) {
    val applications = lazyMap<String, IconSet> { code -> applicationIds[code]?.let { id -> IconSet(id) } }

    companion object {
        val EMPTY = Theme("empty", "Empty", "Empty theme", emptyMap<String, Long>().withDefault { 0L })
    }
}
