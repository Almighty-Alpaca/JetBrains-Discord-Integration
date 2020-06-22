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

@file:UseSerializers(OffsetDateTimeSerializer::class)

package com.almightyalpaca.jetbrains.plugins.discord.analytics.model

import kotlinx.serialization.*
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class Analytics(
    val files: Collection<File> = emptyList(),
    val icons: Collection<Icon> = emptyList(),
    val version: Version? = null
) {
    @Serializable
    data class File(
        val time: OffsetDateTime,
        val editor: String,
        val type: String,
        val extension: String,
        val language: String
    )

    @Serializable
    data class Icon(
        val time: OffsetDateTime,
        val language: String,
        val theme: String,
        val applicationName: String,
        val iconWanted: String,
        val iconUsed: String
    )

    @Serializable
    data class Version(
        val time: OffsetDateTime,
        val version: String,
        val applicationCode: String
    )
}

object OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    override val descriptor: SerialDescriptor = PrimitiveDescriptor(OffsetDateTimeSerializer::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): OffsetDateTime = OffsetDateTime.parse(decoder.decodeString(), formatter)

    override fun serialize(encoder: Encoder, value: OffsetDateTime) = encoder.encodeString(value.format(formatter))
}
