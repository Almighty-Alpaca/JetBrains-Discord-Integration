package com.almightyalpaca.jetbrains.plugins.discord.plugin.analytics

import com.almightyalpaca.jetbrains.plugins.discord.analytics.model.Analytics
import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.dataService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.RenderContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.Renderer
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.ApplicationType
import com.almightyalpaca.jetbrains.plugins.discord.plugin.source.sourceService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.vfs.VirtualFile
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.runBlocking
import java.time.OffsetDateTime

val analyticsService: AnalyticsService
    get() = service()

@Service
class AnalyticsService {
    fun sendData(virtualFile: VirtualFile) {
        runBlocking {
            val context =
                dataService.getData(Renderer.Mode.NORMAL)?.let { RenderContext(sourceService.source, it, Renderer.Mode.NORMAL) }
            val client = HttpClient {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
            }
            val request: Unit = client.post("http://0.0.0.0:8080/analytics") {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Authorization, "token123465789")
                body =
                    Analytics(
                        listOf(
                            Analytics.File(
                                OffsetDateTime.now(),
                                ApplicationType.IDE.toString(),
                                virtualFile.extension!!,
                                virtualFile.fileType.toString())),
                        listOf(
                            Analytics.Icon(
                                OffsetDateTime.now(),
                                virtualFile.extension!!,
                                context?.icons?.theme?.name!!,
                                ApplicationType.IDE.toString(),
                                "thisIcon",
                                "fallback"
                            )),
                        Analytics.Version(
                            OffsetDateTime.now(),
                            "2020.2.1",
                            "Code")
                    )
            }
        }
    }
}
