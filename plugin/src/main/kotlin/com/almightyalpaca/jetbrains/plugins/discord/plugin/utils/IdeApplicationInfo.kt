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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.ApplicationNamesInfo
import java.lang.management.ManagementFactory
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

object IdeApplicationInfo {
    val productCode: String
    val name: String
    val version: String
    val openedAt: OffsetDateTime

    init {
        val applicationInfo = ApplicationInfo.getInstance()
        val applicationNamesInfo = ApplicationNamesInfo.getInstance()

        productCode = applicationInfo.build.productCode
        name = applicationNamesInfo.fullProductNameWithEdition.replace("Edition", "").trim()
        version = applicationInfo.fullVersion
        openedAt = OffsetDateTime.ofInstant(Instant.ofEpochMilli(ManagementFactory.getRuntimeMXBean().startTime), ZoneId.systemDefault())
    }
}

