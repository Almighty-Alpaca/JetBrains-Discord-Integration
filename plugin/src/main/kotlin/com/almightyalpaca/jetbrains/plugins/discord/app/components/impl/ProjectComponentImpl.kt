package com.almightyalpaca.jetbrains.plugins.discord.app.components.impl

import com.almightyalpaca.jetbrains.plugins.discord.app.components.AppComponent
import com.almightyalpaca.jetbrains.plugins.discord.app.components.ProjectComponent
import com.intellij.openapi.project.Project

class ProjectComponentImpl(val project: Project) : ProjectComponent {
    override fun initComponent() {
        AppComponent.instance.app {
            add(project)
        }
    }

    override fun disposeComponent() {
        AppComponent.instance.app {
            remove(project)
        }
    }
}

val Project.component: ProjectComponent
    get() = this.getComponent(ProjectComponent::class.java)
