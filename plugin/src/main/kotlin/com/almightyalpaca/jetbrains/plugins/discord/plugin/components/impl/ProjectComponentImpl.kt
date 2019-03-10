package com.almightyalpaca.jetbrains.plugins.discord.plugin.components.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.ApplicationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.ProjectComponent
import com.intellij.openapi.project.Project

class ProjectComponentImpl(val project: Project) : ProjectComponent {
    override fun initComponent() {
        ApplicationComponent.instance.app {
            add(project)
        }
    }

    override fun disposeComponent() {
        ApplicationComponent.instance.app {
            remove(project)
        }
    }
}

val Project.component: ProjectComponent
    get() = this.getComponent(ProjectComponent::class.java)
