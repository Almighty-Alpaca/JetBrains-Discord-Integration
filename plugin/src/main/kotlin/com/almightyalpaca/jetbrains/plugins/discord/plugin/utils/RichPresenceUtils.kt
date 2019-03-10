package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

import com.jagrosh.discordipc.entities.RichPresence

fun richPresence(block: RichPresence.Builder.() -> Unit): RichPresence = RichPresence.Builder().also(block).build()
