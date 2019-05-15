package com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.ProjectComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.ProjectNotificationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.types.AskShowNotification
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.NewProjectShow
import com.intellij.notification.Notifications
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "DiscordProjectNotificationSettings", storages = [Storage("discord.xml")])
class ProjectNotificationComponentImpl(val project: Project) : ProjectNotificationComponent, PersistentStateComponent<ProjectNotificationComponentImpl.State> {
    private var state: State = State()

    class State {
        var askShowProject: Boolean = settings.newProjectShow.get() == NewProjectShow.ASK
    }

    override fun initComponent() {
        showAskShowProject()
    }

    private fun showAskShowProject(): Unit = with(state) {
        if (askShowProject) {
            val notification = AskShowNotification { show ->
                project.settings.show.set(show)
                askShowProject = false
            }
            Notifications.Bus.notify(notification)
        }
    }

    override fun disposeComponent() {}

    override fun getState(): State? = this.state

    override fun loadState(state: State) {
        XmlSerializerUtil.copyBean(state, this.state)
    }
}

val Project.notifications: ProjectComponent
    get() = this.getComponent(ProjectComponent::class.java)
