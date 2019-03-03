package com.almightyalpaca.jetbrains.plugins.discord.app.components

import com.almightyalpaca.jetbrains.plugins.discord.app.data.ApplicationDataBuilder
import com.almightyalpaca.jetbrains.plugins.discord.app.utils.Application
import com.intellij.openapi.components.BaseComponent

interface AppComponent : BaseComponent {
    fun app(builder: ApplicationDataBuilder.() -> Unit)

    companion object {
        val instance: AppComponent by lazy { Application.instance.getComponent(AppComponent::class.java) }
    }
}
