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

package com.almightyalpaca.jetbrains.plugins.discord.bot.listeners

import com.almightyalpaca.jetbrains.plugins.discord.bot.Settings
import com.almightyalpaca.jetbrains.plugins.discord.bot.utils.modifyRoles
import com.uchuhimo.konf.Config
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.RichPresence
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.user.UserActivityStartEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class UserActivityStartListener(private val config: Config) : ListenerAdapter() {
    override fun onUserActivityStart(event: UserActivityStartEvent) {
        if (config[Settings.development])
            return

        checkActivity(event.member, event.newActivity)
    }

    override fun onGuildReady(event: GuildReadyEvent) {
        if (config[Settings.development])
            return

        for (member in event.guild.memberCache)
            for (activity in member.activities)
                checkActivity(member, activity)
    }

    private fun checkActivity(member: Member, activity: Activity) {
        if (activity !is RichPresence)
            return

        val guild = config[Settings.guilds]
            .asSequence()
            .find { it.id == member.guild.idLong }
            ?: return

        val rolesToAdd = mutableListOf<Long>()
        val rolesToRemove = mutableListOf<Long>()

        for (group in guild.groups) {
            for (role in group.roles) {
                if (role.triggers?.matches(activity) == true) {
                    rolesToAdd -= group.allRoleIds
                    rolesToAdd += role.id

                    rolesToRemove += group.allRoleIds
                    rolesToRemove -= role.id
                }
            }
        }

        member.modifyRoles(rolesToAdd, rolesToRemove).queue()
    }
}
