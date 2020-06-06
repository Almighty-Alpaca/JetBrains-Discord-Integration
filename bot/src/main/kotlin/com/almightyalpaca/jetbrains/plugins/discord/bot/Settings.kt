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

import com.uchuhimo.konf.ConfigSpec
import net.dv8tion.jda.api.entities.RichPresence
import java.util.*

object Settings : ConfigSpec("") {
    val token by required<String>()

    val owner by required<Long>()
    val coOwners by optional<Set<Long>>(Collections.emptySet())

    val command_prefix by required<String>()

    val development by optional(false)

    val guilds by required<Set<Guild>>()

    data class Guild(val name: String? = null, val id: Long, val groups: Set<Group>) {
        data class Group(val name: String? = null, val roles: Set<Role>) {
            val allRoleIds = roles.map { it.id }

            data class Role(val name: String? = null, val id: Long, val triggers: Trigger?) {
                data class Trigger(
                    val appIds: Set<Long> = setOf(),
                    val states: Set<String> = setOf(),
                    val details: Set<String> = setOf(),
                    val partyIds: Set<String> = setOf(),
                    val largeImageKeys: Set<String> = setOf(),
                    val largeImageTexts: Set<String> = setOf(),
                    val smallImageKeys: Set<String> = setOf(),
                    val smallImageTexts: Set<String> = setOf()
                ) {
                    fun matches(presence: RichPresence): Boolean {
                        return presence.applicationIdLong in appIds
                                || presence.state in states
                                || presence.details in details
                                || presence.party?.id in partyIds
                                || presence.largeImage?.key in largeImageKeys
                                || presence.largeImage?.text in largeImageTexts
                                || presence.smallImage?.key in smallImageKeys
                                || presence.smallImage?.text in smallImageTexts
                    }
                }
            }
        }
    }
}
