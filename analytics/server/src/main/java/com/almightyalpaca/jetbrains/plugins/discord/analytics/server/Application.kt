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

package com.almightyalpaca.jetbrains.plugins.discord.analytics.server

import com.almightyalpaca.jetbrains.plugins.discord.analytics.model.Data
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.AutoHeadResponse
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>): Unit = EngineMain.main(args)

@JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(AutoHeadResponse)

    install(DefaultHeaders) {
        // header("X-Engine", "Ktor") // will send this header with each response
    }

    install(Authentication) {
        // TODO: replace with token based authentication
        // basic("myBasicAuth") {
        //     realm = "Ktor Server"
        //     validate { if (it.name == "test" && it.password == "password") UserIdPrincipal(it.name) else null }
        // }
    }

    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/example") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        post("/data") {
            val data = call.receive<Data>()

            call.respond(mapOf("hello" to "world"))
        }

        // authenticate("myBasicAuth") {
        //     get("/protected/route/basic") {
        //         val principal = call.principal<UserIdPrincipal>()!!
        //         call.respondText("Hello ${principal.name}")
        //     }
        // }
    }
}
