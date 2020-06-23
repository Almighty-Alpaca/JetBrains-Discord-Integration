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

import com.almightyalpaca.jetbrains.plugins.discord.analytics.server.ktor.configurationEnvironment
import com.almightyalpaca.jetbrains.plugins.discord.analytics.server.ktor.token
import com.almightyalpaca.jetbrains.plugins.discord.analytics.server.services.Configuration
import com.almightyalpaca.jetbrains.plugins.discord.analytics.server.services.Database
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stop
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI
import org.koin.dsl.module
import org.koin.experimental.builder.single
import org.koin.ktor.ext.Koin
import org.koin.logger.slf4jLogger
import java.util.concurrent.TimeUnit

@KtorExperimentalAPI
fun runServer(configuration: Configuration) {
    val environment = configurationEnvironment(configuration.ktor) {
        module { main(configuration) }
    }

    embeddedServer(Netty, environment).apply {
        addShutdownHook {
            stop(3, 5, TimeUnit.SECONDS)
        }

        start(true)
    }
}

fun Application.main(configuration: Configuration) {
    install(AutoHeadResponse)

    install(DefaultHeaders) {
        // header("X-Engine", "Ktor") // will send this header with each response
    }

    install(Authentication) {
        token("analyticsAuth") {
            validate(tokens(configuration.authentication?.tokens ?: emptySet()))
        }
    }

    install(ContentNegotiation) {
        json()
    }

    install(CallLogging)

    install(Koin) {
        slf4jLogger()

        modules(module {
            single { configuration }
            single<Database>(createOnStart = true)
        })
    }

    routing {
        test()

        route("api") {
            route("v1") {
                analytics()
            }
        }
    }
}
