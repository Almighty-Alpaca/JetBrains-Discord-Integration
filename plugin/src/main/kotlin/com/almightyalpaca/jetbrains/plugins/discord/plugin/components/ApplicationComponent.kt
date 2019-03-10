package com.almightyalpaca.jetbrains.plugins.discord.plugin.components

import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.ApplicationDataBuilder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Application
import com.intellij.openapi.components.BaseComponent

interface ApplicationComponent : BaseComponent {
    fun app(builder: ApplicationDataBuilder.() -> Unit)

    companion object {
        val instance: ApplicationComponent by lazy { Application.instance.getComponent(ApplicationComponent::class.java) }
    }
}
