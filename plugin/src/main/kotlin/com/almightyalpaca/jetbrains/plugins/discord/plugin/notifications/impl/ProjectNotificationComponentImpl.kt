package com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.ProjectComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.ProjectNotificationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.notificationSettings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.types.AskShowNotification
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project

class ProjectNotificationComponentImpl(val project: Project) : ProjectNotificationComponent {
    override fun initComponent() {
        showAskShowProject()
    }

    private fun showAskShowProject() {
        if (project.notificationSettings.askShowProject) {
            val notification = AskShowNotification { show ->
                project.settings.show.set(show)
                project.notificationSettings.askShowProject = false
            }
            Notifications.Bus.notify(notification)
        }
    }

    override fun disposeComponent() {}
}

val Project.notifications: ProjectComponent
    get() = this.getComponent(ProjectComponent::class.java)
