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

package com.almightyalpaca.jetbrains.plugins.discord.analytics.server.services

import com.almightyalpaca.jetbrains.plugins.discord.analytics.model.Analytics
import com.almightyalpaca.jetbrains.plugins.discord.analytics.server.database.generated.Tables.*
import com.almightyalpaca.jetbrains.plugins.discord.analytics.server.jooq.upsertNameReturningId
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.ttddyy.dsproxy.ExecutionInfo
import net.ttddyy.dsproxy.QueryInfo
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder
import org.flywaydb.core.Flyway
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.TransactionalRunnable
import org.jooq.impl.DSL
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import javax.sql.DataSource

class Database : KoinComponent {
    private val logger: Logger = LoggerFactory.getLogger("Database")

    private val configuration by inject<Configuration>()

    private val dataSource: DataSource
    private val dsl: DSLContext

    init {
        val config = HikariConfig()

        config.jdbcUrl = configuration.database.url
        config.username = configuration.database.username
        config.password = configuration.database.password

        config.initializationFailTimeout = 60_000

        val hikariDataSource = HikariDataSource(config)

        dataSource = ProxyDataSourceBuilder
            .create(hikariDataSource)
            .listener(object : SLF4JQueryLoggingListener() {
                override fun afterQuery(execInfo: ExecutionInfo, queryInfoList: MutableList<QueryInfo>) {
                    super.afterQuery(execInfo, queryInfoList)
                    logger.debug("Query took ${execInfo.elapsedTime}msec")
                }
            })
            .proxyResultSet()
            .build()

        val flyway = Flyway
            .configure()
            .dataSource(dataSource)
            .baselineOnMigrate(true)
            .repeatableSqlMigrationPrefix("r")
            .sqlMigrationPrefix("v")
            .sqlMigrationSeparator("-")
            .load()

        flyway.migrate()

        dsl = DSL.using(dataSource, SQLDialect.POSTGRES)
    }

    private fun <T> withConnection(block: Connection.() -> T): T = dataSource.connection.use(block)

    private fun <T> withContext(block: DSLContext.() -> T): T = block(dsl)

    private fun <T> withTransaction(block: DSLContext.() -> T): T = withContext {
        var result: T? = null

        transaction(TransactionalRunnable {
            result = block(this)
        })

        return@withContext result!!
    }

    fun insert(file: Analytics.File) = withTransaction {
        val editorId = upsertNameReturningId(EDITORS, EDITORS.ID, EDITORS.NAME, file.editor.takeLast(64))
        val extensionId = upsertNameReturningId(EXTENSIONS, EXTENSIONS.ID, EXTENSIONS.NAME, file.extension.takeLast(16))
        val languageId = upsertNameReturningId(LANGUAGES, LANGUAGES.ID, LANGUAGES.NAME, file.language.takeLast(16))

        insertInto(FILE_STATS)
            .columns(FILE_STATS.TIME, FILE_STATS.EDITOR_ID, FILE_STATS.EXTENSION, FILE_STATS.LANGUAGE_ID)
            .values(file.time, editorId, extensionId, languageId)
            .execute()
    }

    fun insert(icon: Analytics.Icon) = withTransaction {
        val languageId = upsertNameReturningId(LANGUAGES, LANGUAGES.ID, LANGUAGES.NAME, icon.language.takeLast(16))
        val themeId = upsertNameReturningId(THEMES, THEMES.ID, THEMES.NAME, icon.theme.takeLast(16))
        val applicationNameId = upsertNameReturningId(APPLICATION_NAMES, APPLICATION_NAMES.ID, APPLICATION_NAMES.NAME, icon.applicationName.takeLast(32))
        val iconWantedId = upsertNameReturningId(ICONS, ICONS.ID, ICONS.NAME, icon.iconWanted.takeLast(32))
        val iconUsedId = upsertNameReturningId(ICONS, ICONS.ID, ICONS.NAME, icon.iconUsed.takeLast(32))

        insertInto(ICON_STATS)
            .columns(ICON_STATS.TIME, ICON_STATS.LANGUAGE_ID, ICON_STATS.THEME_ID, ICON_STATS.APPLICATION_NAME_ID, ICON_STATS.ICON_WANTED_ID, ICON_STATS.ICON_USED_ID)
            .values(icon.time, languageId, themeId, applicationNameId, iconWantedId, iconUsedId)
            .execute()
    }

    fun insert(version: Analytics.Version) = withTransaction {
        val versionId = upsertNameReturningId(VERSIONS, VERSIONS.ID, VERSIONS.VERSION, version.version.takeLast(16))
        val applicationCodeId = upsertNameReturningId(APPLICATION_CODES, APPLICATION_CODES.ID, APPLICATION_CODES.CODE, version.applicationCode.takeLast(8))

        insertInto(VERSION_STATS)
            .columns(VERSION_STATS.TIME, VERSION_STATS.VERSION_ID, VERSION_STATS.APPLICATION_CODE_ID)
            .values(version.time, versionId, applicationCodeId)
            .execute()
    }
}
