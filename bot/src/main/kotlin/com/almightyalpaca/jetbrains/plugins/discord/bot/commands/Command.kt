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

package com.almightyalpaca.jetbrains.plugins.discord.bot.commands

import com.almightyalpaca.jetbrains.plugins.discord.bot.Settings
import com.uchuhimo.konf.Config
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.SubscribeEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

abstract class Command(private val config: Config, name: String, description: String, private val adminOnly: Boolean = false) : CommandData(name, description), EventListener {
    @SubscribeEvent
    final override fun onEvent(event: GenericEvent) {
        if (event !is SlashCommandEvent)
            return

        if (event.guild == null)
            return

        if (event.name != this.name)
            return

        if (adminOnly && event.user.idLong !in config[Settings.admins])
            return

        event.deferReply(true).queue()

        execute(event)
    }

    abstract fun execute(event: SlashCommandEvent)
}
