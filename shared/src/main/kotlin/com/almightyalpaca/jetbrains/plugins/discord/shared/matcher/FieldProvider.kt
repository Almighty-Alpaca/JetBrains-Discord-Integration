package com.almightyalpaca.jetbrains.plugins.discord.shared.matcher

interface FieldProvider {
    fun getField(target: Matcher.Target): Collection<String>
}
