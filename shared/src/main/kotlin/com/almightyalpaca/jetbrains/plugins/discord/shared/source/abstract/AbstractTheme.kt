package com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Theme

abstract class AbstractTheme(override val id: String, override val name: String, override val description: String, override val applications: Map<String, Long>) : Theme
