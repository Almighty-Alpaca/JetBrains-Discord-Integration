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

package com.almightyalpaca.jetbrains.plugins.discord.analytics.server.services

data class Configuration(
    val database: Database,
    val ktor: Ktor,
    val authentication: Authentication?
) {

    data class Database(
        val url: String,
        val username: String,
        val password: String
    )

    data class Ktor(
        val application: Application?,
        val deployment: Deployment,
        val security: Security?
    ) {
        data class Application(val id: String)

        data class Deployment(
            val host: String?,
            val port: Int?,
            val watch: List<String>?,
            val rootPath: String?,
            val sslPort: Int?
        )

        data class Security(
            val ssl: Ssl
        ) {
            data class Ssl(
                val keyStore: String,
                val keyAlias: String,
                val keyStorePassword: String,
                val privateKeyPassword: String
            )
        }
    }

    data class Authentication(
        val tokens: Set<String>?
    )
}
