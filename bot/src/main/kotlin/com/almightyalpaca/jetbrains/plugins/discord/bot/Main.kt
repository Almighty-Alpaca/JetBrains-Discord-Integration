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

package com.almightyalpaca.jetbrains.plugins.discord.bot

import com.almightyalpaca.jetbrains.plugins.discord.bot.commands.EvalCommand
import com.almightyalpaca.jetbrains.plugins.discord.bot.commands.PingCommand
import com.almightyalpaca.jetbrains.plugins.discord.bot.commands.ShutdownCommand
import com.almightyalpaca.jetbrains.plugins.discord.bot.listeners.UserActivityStartListener
import com.almightyalpaca.jetbrains.plugins.discord.bot.utils.setCoOwnerIds
import com.almightyalpaca.jetbrains.plugins.discord.bot.utils.setOwnerId
import com.jagrosh.jdautilities.command.CommandClientBuilder
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import java.nio.file.Paths

val isDocker by lazy { System.getenv("DOCKER") != null }

fun main() {
    val configFolder = when {
        isDocker -> Paths.get("/config")
        else -> Paths.get(".")
    }

    val config = Config {
        addSpec(Settings)
    }
        .from.yaml.file(configFolder.resolve("config.yaml").toFile())
        .from.env()
        .from.systemProperties()

    val client = CommandClientBuilder()
        .setOwnerId(config[Settings.owner])
        .setPrefix(config[Settings.command_prefix])
        .setCoOwnerIds(config[Settings.coOwners])
        // .setGame(Game.of(config[Bot.Status.type], config[Bot.Status.name], config[Bot.Status.url]))
        .setActivity(Activity.playing("with roles"))
        // .setServerInvite(config[Guild.invite])
        .addCommand(EvalCommand(config))
        .addCommand(PingCommand())
        .addCommand(ShutdownCommand())
        .setShutdownAutomatically(true)
        .build()

    JDABuilder()
        .setToken(config[Settings.token])
        .addEventListeners(client)
        .addEventListeners(UserActivityStartListener(config))
        .build()
}
