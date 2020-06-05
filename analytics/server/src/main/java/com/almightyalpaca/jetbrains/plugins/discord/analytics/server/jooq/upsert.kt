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

package com.almightyalpaca.jetbrains.plugins.discord.analytics.server.jooq

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record2
import org.jooq.Table
import org.jooq.impl.DSL

fun <T_Id, T_Column> DSLContext.upsertNameReturningId(table: Table<*>, id: Field<T_Id>, column: Field<T_Column>, value: T_Column): T_Id {
    val upsert = DSL
        .insertInto(table)
        .columns(column)
        .values(value)
        .onConflict(column)
        .doNothing()
        .returningResult(id)

    val select = DSL
        .select(id)
        .from(table)
        .where(column.equal(value))

    val result = fetchOne("WITH input AS ({0}) {1} UNION ALL TABLE input", upsert, select)

    return result[id]
}
