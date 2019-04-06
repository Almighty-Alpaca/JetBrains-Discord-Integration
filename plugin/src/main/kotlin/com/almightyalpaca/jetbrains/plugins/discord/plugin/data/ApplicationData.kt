package com.almightyalpaca.jetbrains.plugins.discord.plugin.data

import com.almightyalpaca.jetbrains.plugins.discord.shared.themes.Theme
import com.almightyalpaca.jetbrains.plugins.discord.shared.themes.icons.IconSet
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.toMap
import com.intellij.openapi.project.Project
import java.time.OffsetDateTime

class ApplicationData(val id: String, val version: String, val openedAt: OffsetDateTime = OffsetDateTime.now(), projects: Collection<ProjectData> = emptyList()) : AccessedAt {
    val projects = projects.toMap { p -> p.platformProject to p }

    override val accessedAt: OffsetDateTime
        get() = projects.maxBy { it.value.accessedAt }?.value?.accessedAt ?: openedAt

    fun builder() = ApplicationDataBuilder(id, version, openedAt, projects)

    companion object {
        val DEFAULT by lazy {
            com.intellij.openapi.application.ApplicationInfo.getInstance()
                    .run { ApplicationData(build.productCode, fullVersion) }
        }
    }

    override fun toString(): String = "ApplicationData" // ObjectMapper().writeValueAsString(this)
}

class ApplicationDataBuilder(var id: String, var version: String, val openedAt: OffsetDateTime = OffsetDateTime.now(), projects: Map<Project, ProjectData> = emptyMap()) {
    private val projects = mutableMapOf(*projects.map { (k, v) -> k to v.builder() }.toTypedArray())

    fun add(project: Project?, builder: ProjectDataBuilder.() -> Unit = {}) {
        project?.let { projects.computeIfAbsent(project) { ProjectDataBuilder(project, project.name) }.builder() }
    }

    fun update(project: Project?, builder: ProjectDataBuilder.() -> Unit) {
        projects[project]?.builder()
    }

    infix fun remove(project: Project?) {
        project?.let { projects.remove(project) }
    }

    operator fun contains(project: Project?) = project != null && project in projects

    fun build() = ApplicationData(id, version, openedAt, projects.values.map(ProjectDataBuilder::build))
}

operator fun Theme.get(app: ApplicationData): IconSet = applications[app.id]!!
