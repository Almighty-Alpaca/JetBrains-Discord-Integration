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

import com.almightyalpaca.jetbrains.plugins.discord.analytics.server.services.Configuration
import com.sksamuel.hoplite.ConfigFilePropertySource
import com.sksamuel.hoplite.ConfigSource
import com.sksamuel.hoplite.parsers.ParserRegistry
import com.sksamuel.hoplite.parsers.defaultParserRegistry
import io.ktor.server.engine.*
import io.ktor.util.KtorExperimentalAPI
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore

@Suppress("UNUSED_VARIABLE")
@KtorExperimentalAPI
fun configurationEnvironment(config: Configuration.Ktor, configure: ApplicationEngineEnvironmentBuilder.() -> Unit): ApplicationEngineEnvironment {
    val applicationIdPath = "ktor.application.id"

    val hostConfigPath = "deployment.host"
    val hostPortPath = "deployment.port"
    val hostWatchPaths = "deployment.watch"

    val rootPathPath = "deployment.rootPath"

    val hostSslPortPath = "deployment.sslPort"
    val hostSslKeyStore = "security.ssl.keyStore"
    val hostSslKeyAlias = "security.ssl.keyAlias"
    val hostSslKeyStorePassword = "security.ssl.keyStorePassword"
    val hostSslPrivateKeyPassword = "security.ssl.privateKeyPassword"

    return applicationEngineEnvironment {
        // config = HopliteApplicationConfig(node)

        val applicationId = config.application?.id ?: "Application"
        log = LoggerFactory.getLogger(applicationId)

        rootPath = config.deployment.rootPath ?: ""

        val host = config.deployment.host ?: "0.0.0.0"
        val port = config.deployment.port
        val sslPort = config.deployment.sslPort
        val sslKeyStorePath = config.security?.ssl?.keyStore
        val sslKeyStorePassword = config.security?.ssl?.keyStorePassword?.trim()
        val sslPrivateKeyPassword = config.security?.ssl?.privateKeyPassword?.trim()
        val sslKeyAlias = config.security?.ssl?.keyAlias ?: "mykey"

        if (port != null) {
            connector {
                this.host = host
                this.port = port.toInt()
            }
        }

        if (sslPort != null) {
            if (sslKeyStorePath == null) {
                throw IllegalArgumentException("SSL requires keystore: use -sslKeyStore=path or $hostSslKeyStore config")
            }
            if (sslKeyStorePassword == null) {
                throw IllegalArgumentException("SSL requires keystore password: use $hostSslKeyStorePassword config")
            }
            if (sslPrivateKeyPassword == null) {
                throw IllegalArgumentException("SSL requires certificate password: use $hostSslPrivateKeyPassword config")
            }

            val keyStoreFile = File(sslKeyStorePath).let { file ->
                if (file.exists() || file.isAbsolute)
                    file
                else
                    File(".", sslKeyStorePath).absoluteFile
            }
            val keyStore = KeyStore.getInstance("JKS").apply {
                FileInputStream(keyStoreFile).use {
                    load(it, sslKeyStorePassword.toCharArray())
                }

                requireNotNull(getKey(sslKeyAlias, sslPrivateKeyPassword.toCharArray()) == null) {
                    "The specified key $sslKeyAlias doesn't exist in the key store $sslKeyStorePath"
                }
            }

            sslConnector(keyStore, sslKeyAlias,
                { sslKeyStorePassword.toCharArray() },
                { sslPrivateKeyPassword.toCharArray() }) {
                this.host = host
                this.port = sslPort.toInt()
                keyStorePath = keyStoreFile
            }
        }

        if (port == null && sslPort == null) {
            throw IllegalArgumentException("Neither port nor sslPort specified")
        }

        this.config.propertyOrNull(hostWatchPaths)?.getList()?.let {
            watchPaths = it
        }

        configure()
    }
}
