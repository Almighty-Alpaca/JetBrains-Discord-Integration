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

package com.almightyalpaca.jetbrains.plugins.discord.bot.utils

import net.dv8tion.jda.api.entities.Activity

fun Activity.of(type: Activity.ActivityType? = null, name: String, url: String? = null): Activity? {
    return when (type) {
        Activity.ActivityType.STREAMING ->
            when (url) {
                null -> null
                else -> Activity.streaming(name, url)
            }
        null -> null
        else -> Activity.of(type, name)

    }
}

