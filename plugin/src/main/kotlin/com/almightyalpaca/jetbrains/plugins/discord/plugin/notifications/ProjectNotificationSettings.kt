package com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.NewProjectShow
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "ProjectNotificationSettings", storages = [Storage("discord.xml")])
class ProjectNotificationSettings : PersistentStateComponent<ProjectNotificationSettings> {
    var askShowProject: Boolean = settings.newProjectShow.get() == NewProjectShow.ASK

    override fun getState(): ProjectNotificationSettings? = this

    override fun loadState(state: ProjectNotificationSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }
}

val Project.notificationSettings: ProjectNotificationSettings
    get() = ServiceManager.getService(this, ProjectNotificationSettings::class.java)
