package com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.IconSet
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Theme

abstract class AbstractIconSet(override val theme: Theme, override val applicationId: Long?, icons: Set<String>, override val applicationCode: String) : IconSet, Set<String> by icons
