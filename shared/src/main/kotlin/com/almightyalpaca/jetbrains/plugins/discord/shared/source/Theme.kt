package com.almightyalpaca.jetbrains.plugins.discord.shared.source

interface Theme {
    val id: String
    val name: String
    val description: String
    val applications: Map<String, Long>

    fun getIconSet(applicationCode: String): IconSet?
}
