package com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications

import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.getComponent
import com.intellij.openapi.components.BaseComponent

interface ApplicationNotificationComponent : BaseComponent

val notifications: ApplicationNotificationComponent
    get() = getComponent(ApplicationNotificationComponent::class)
