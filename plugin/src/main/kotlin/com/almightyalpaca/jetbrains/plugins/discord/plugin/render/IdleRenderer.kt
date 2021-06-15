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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.render

import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresence
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Plugin
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

class IdleRenderer(context: RenderContext) : Renderer(context) {
    override fun RenderContext.render(): RichPresence {

        return RichPresence(context.icons?.applicationId) presence@{
            this@presence.details = "Idling"

            this@presence.startTimestamp = idleData?.idleTimestamp?.let {
                OffsetDateTime.ofInstant(
                    Instant.ofEpochMilli(it),
                    ZoneId.systemDefault()
                )
            }

            this@presence.largeImage = icons?.let { icons -> RichPresence.Image(icons.getAsset("application"), "Idling") }

            this.partyId = Plugin.version?.toString()
        }
    }
}
