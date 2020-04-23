/*
 * Copyright 2017-2019 Aljoscha Grebe
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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.diagnose

import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.DisposableCoroutineScope
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.tryOrDefault
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.application.ApplicationNamesInfo
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.extensions.PluginId
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import org.apache.commons.lang3.SystemUtils
import java.nio.charset.StandardCharsets
import java.util.*

val diagnoseService: DiagnoseService
    get() = service()

@Service
class DiagnoseService : DisposableCoroutineScope {
    override val parentJob: Job = SupervisorJob()

    val discord = async(start = CoroutineStart.DEFAULT) { tryOrDefault(Discord.OTHER) { readDiscord() } }
    val plugins = async(start = CoroutineStart.DEFAULT) { tryOrDefault(Plugins.NONE) { readPlugins() } }
    val ide = async(start = CoroutineStart.DEFAULT) { tryOrDefault(IDE.OTHER) { readIDE() } }

    private fun readDiscord(): Discord = when {
        SystemUtils.IS_OS_WINDOWS -> readDiscordWindows()
        SystemUtils.IS_OS_LINUX -> readDiscordLinux()
        SystemUtils.IS_OS_MAC -> readDiscordMac()
        else -> Discord.OTHER
    }

    private fun readDiscordMac(): Discord {
        val clients = arrayOf(
            "Discord.app",
            "Discord PTB.app",
            "Discord Canary.app",
            "Discord Development.app"
        )

        val process = Runtime.getRuntime().exec("ps -A")
        process.waitFor()

        val clientNotRunning = process.inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
            reader.lineSequence()
                .none { line -> clients.any { client -> line.contains(client) } }
        }

        if (clientNotRunning) {
            return Discord.CLOSED
        }

        // TODO: Mac Discord browser detection
        return Discord.OTHER
    }

    private fun readDiscordLinux(): Discord {
        val process = Runtime.getRuntime().exec("ps ax")
        process.waitFor()
        val lines = process.inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
            reader.lineSequence()
                .filter { line -> line.contains("discord", true) }
                .joinToString("\n")
        }

        if (lines.isBlank()) {
            return Discord.CLOSED
        } else if (lines.contains("/snap/discord/", true)) {
            return Discord.SNAP
        }

        // TODO: Linux Discord browser detection
        return Discord.OTHER
    }

    private fun readDiscordWindows(): Discord {
        val browsers = arrayOf(
            "chrome.exe",
            "firefox.exe",
            "ApplicationFrameHost.exe", // Microsoft Edge
            "opera.exe",
            "iexplore.exe"
        )

        val discord = arrayOf(
            "Discord.exe",
            "DiscordPTB.exe",
            "DiscordCanary.exe",
            "DiscordDevelopment.exe"
        )

        val process = Runtime.getRuntime().exec("""tasklist /V /fi "SESSIONNAME eq Console"""")
        val lines = process.inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
            reader.lineSequence()
                .filter { line -> line.contains("Discord", true) }
                .toList()
        }

        if (lines.isEmpty()) {
            return Discord.CLOSED
        } else {
            val discordClientNotRunning = lines.none { line -> discord.any { exe -> line.startsWith(exe, true) } }
            if (discordClientNotRunning) {
                val discordBrowser = lines.any { line ->
                    line.contains("discord", true) && browsers.any { browser ->
                        line.startsWith(
                            browser,
                            true
                        )
                    }
                }
                if (discordBrowser) {
                    return Discord.BROWSER
                }
            }
        }

        return Discord.OTHER
    }

    private val pluginsIds = arrayOf(
        "com.tsunderebug.discordintellij",
        "com.my.fobes.intellij.discord"
    )

    private fun readPlugins(): Plugins {
        val matches = PluginManager.getPlugins()
            .asSequence()
            .map(IdeaPluginDescriptor::getPluginId)
            .map(PluginId::getIdString)
            .filter(Objects::nonNull)
            .count(pluginsIds::contains)

        return when (matches) {
            0 -> Plugins.NONE
            1 -> Plugins.ONE
            else -> Plugins.MULTIPLE
        }
    }

    private fun readIDE(): IDE = when {
        SystemUtils.IS_OS_WINDOWS -> readIDEWindows()
        SystemUtils.IS_OS_LINUX -> readIDELinux()
        SystemUtils.IS_OS_MAC -> readIDEMac()
        else -> IDE.OTHER
    }

    private fun readIDEWindows(): IDE = IDE.OTHER

    private fun readIDELinux(): IDE {
        if (System.getenv("SNAP") != null) {
            return IDE.SNAP
        }

        return IDE.OTHER
    }

    private fun readIDEMac(): IDE = IDE.OTHER

    // override fun reportDiscordConnectionChange() = TODO("not implemented")
    // override fun reportInternetConnectionChange() = TODO("not implemented")

    enum class Discord(val message: String) {
        SNAP("It seems like Discord is running in a Snap package. This will most likely prevent the plugin from connecting to your Discord client!"),
        BROWSER("It seems like Discord is running in the browser. The plugin will not be able to connect to the Discord client!"),
        CLOSED("Could not detect a running Discord client!"),
        OTHER("")
    }

    enum class Plugins(val message: String) {
        NONE(""),
        ONE("It seems like you have another Rich Presence plugin installed. Please uninstall it to avoid conflicts!"),
        MULTIPLE("It seems like you have multiple other Rich Presence plugin installed. Please uninstall them to avoid conflicts!")
    }

    enum class IDE(val message: String) {
        SNAP("${ApplicationNamesInfo.getInstance().fullProductName} is running as a Snap package. This will most likely prevent the plugin from connection to your Discord client!"),
        OTHER("")
    }
}
