/*
 * Copyright 2017-2020 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almightyalpaca.jetbrains.plugins.discord.analytics.server.routes

import com.almightyalpaca.jetbrains.plugins.discord.analytics.model.Analytics
import com.almightyalpaca.jetbrains.plugins.discord.analytics.server.koin.inject
import com.almightyalpaca.jetbrains.plugins.discord.analytics.server.services.Database
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import mu.KotlinLogging

object AnalyticsRoute {
    private val log = KotlinLogging.logger {}

    fun Route.analytics() {
        authenticate("analyticsAuth") {
            post("/analytics") { analytics: Analytics ->
                val database: Database by inject()

                analytics.files.forEach { file ->
                    log.info { "File: $file" }

                    database.insert(file)
                }

                analytics.icons.forEach { icon ->
                    log.info { "Icon: $icon" }

                    database.insert(icon)
                }

                analytics.version?.let { version ->
                    log.info { "Version: $version" }

                    database.insert(version)
                }

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
