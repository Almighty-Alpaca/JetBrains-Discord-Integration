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

package com.almightyalpaca.jetbrains.plugins.discord.analytics.server.ktor

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.*
import io.ktor.request.ApplicationRequest
import io.ktor.request.authorization
import io.ktor.response.respond
import io.ktor.util.KtorExperimentalAPI

/**
 * Represents a simple token credential
 * @property token
 */
data class TokenCredential(val token: String) : Credential

/**
 * Represents a simple principal
 */
class SimplePrincipal : Principal

/**
 * Represents a Token authentication provider
 * @property name is the name of the provider, or `null` for a default provider
 */
class TokenAuthenticationProvider internal constructor(
    configuration: Configuration
) : AuthenticationProvider(configuration) {
    internal val authenticationFunction = configuration.authenticationFunction

    /**
     * Token auth configuration
     */
    class Configuration internal constructor(name: String?) : AuthenticationProvider.Configuration(name) {
        internal var authenticationFunction: AuthenticationFunction<TokenCredential> = {
            throw NotImplementedError(
                "Token auth validate function is not specified. Use token { validate { ... } } to fix."
            )
        }

        /**
         * Sets a validation function that will check given [TokenCredential] instance and return [Principal],
         * or null if credential does not correspond to an authenticated principal
         */
        fun validate(body: suspend ApplicationCall.(TokenCredential) -> Principal?) {
            authenticationFunction = body
        }

        fun token(token: String): suspend ApplicationCall.(TokenCredential) -> Principal? {
            return { if (it.token == token) SimplePrincipal() else null }
        }

        fun tokens(tokens: Collection<String>): suspend ApplicationCall.(TokenCredential) -> Principal? {
            return { if (it.token in tokens) SimplePrincipal() else null }
        }
    }
}

/**
 * Installs Token Authentication mechanism
 */
fun Authentication.Configuration.token(
    name: String? = null,
    configure: TokenAuthenticationProvider.Configuration.() -> Unit
) {
    val provider = TokenAuthenticationProvider(TokenAuthenticationProvider.Configuration(name).apply(configure))
    val authenticate = provider.authenticationFunction

    provider.pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
        val credentials = call.request.tokenAuthenticationCredentials()
        val principal = credentials?.let { authenticate(call, it) }

        val cause = when {
            credentials == null -> AuthenticationFailedCause.NoCredentials
            principal == null -> AuthenticationFailedCause.InvalidCredentials
            else -> null
        }

        if (cause != null) {
            context.challenge(tokenAuthenticationChallengeKey, cause) {
                call.respond(UnauthorizedResponse())
                it.complete()
            }
        }
        if (principal != null) {
            context.principal(principal)
        }
    }

    register(provider)
}

/**
 * Retrieves Token authentication credentials for this [ApplicationRequest]
 */
@Suppress("EXPERIMENTAL_API_USAGE")
fun ApplicationRequest.tokenAuthenticationCredentials(): TokenCredential? {
    return when (val authHeader = authorization()) {
        is String -> TokenCredential(authHeader)
        else -> null
    }
}

private val tokenAuthenticationChallengeKey: Any = "TokenAuth"

