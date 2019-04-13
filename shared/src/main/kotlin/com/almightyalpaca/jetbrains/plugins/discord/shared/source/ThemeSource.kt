package com.almightyalpaca.jetbrains.plugins.discord.shared.source

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.local.LocalSource
import com.fasterxml.jackson.databind.JsonNode

class ThemeSource(val id: String, val node: JsonNode)