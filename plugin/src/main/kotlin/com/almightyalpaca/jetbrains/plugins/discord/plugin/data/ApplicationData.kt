package com.almightyalpaca.jetbrains.plugins.discord.plugin.data

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.IconSet
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Theme
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.toMap
import com.intellij.openapi.application.ApplicationNamesInfo
import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.project.Project
import java.time.OffsetDateTime

class ApplicationData(
    val id: String,
    val name: String,
    val version: String,
    val openedAt: OffsetDateTime = OffsetDateTime.now(),
    projects: Collection<ProjectData> = emptyList()
) : AccessedAt {
    val projects = projects.toMap { p -> p.platformProject to p }

    override val accessedAt: OffsetDateTime
        get() = projects.maxBy { it.value.accessedAt }?.value?.accessedAt ?: openedAt

    fun builder() = ApplicationDataBuilder(id, name, version, openedAt, projects)

    companion object {
        val DEFAULT by lazy {
            val appInfo = ApplicationInfoEx.getInstance()
            val appNameInfo = ApplicationNamesInfo.getInstance()

            val edition = appNameInfo.fullProductNameWithEdition.replace("Edition", "").trim()

            ApplicationData(appInfo.build.productCode, edition, appInfo.fullVersion)
        }
    }

    override fun toString(): String = "ApplicationData" // ObjectMapper().writeValueAsString(this)
}

class ApplicationDataBuilder(var id: String, var name: String, var version: String, val openedAt: OffsetDateTime = OffsetDateTime.now(), projects: Map<Project, ProjectData> = emptyMap()) {
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

    fun build() = ApplicationData(id, name, version, openedAt, projects.values.map(ProjectDataBuilder::build))
}

fun Theme.getIconSet(app: ApplicationData): IconSet? = getIconSet(app.id)
