package com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "ApplicationNotificationSettings", storages = [Storage("discord.xml")])
class ApplicationNotificationSettings : PersistentStateComponent<ApplicationNotificationSettings> {
    var lastUpdateNotification: String? = null

    override fun getState(): ApplicationNotificationSettings? = this

    override fun loadState(state: ApplicationNotificationSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }
}

val notificationSettings: ApplicationNotificationSettings
    get() = ServiceManager.getService(ApplicationNotificationSettings::class.java)
