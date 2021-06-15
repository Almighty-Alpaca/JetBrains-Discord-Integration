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

import com.uchuhimo.konf.Config
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import javax.script.ScriptEngineManager
import javax.script.ScriptException

class EvalCommand(private val config: Config) : Command(config, "eval", "Run a Kotlin script", true) {
    init {
        addOption(OptionType.STRING, "script", "The script to execute", true)
    }

    private val manager = ScriptEngineManager()

    override fun execute(event: SlashCommandEvent) {
        val engine = manager.getEngineByExtension("kts")

        with(EvalVars) {
            user = event.user
            member = event.member!!
            channel = event.textChannel
            guild = event.guild!!
            config = this@EvalCommand.config
        }

        val startTime = System.nanoTime()

        val result = try {
            val script = """
                ${EvalVars::class.qualifiedName}.*
                
                ${event.getOption("script")!!.asString}
            """

            engine.eval(script)
        } catch (e: ScriptException) {
            e
        }

        val endTime = System.nanoTime()
        val timeUsed = endTime - startTime

        val response = "Executed in ${timeUsed}ns"

        val message: String = when {
            result is Exception -> {
                result.printStackTrace()

                val cause = result.cause

                if (cause == null) {
                    "$response with ${result.javaClass.simpleName}: ${result.message} on line ${result.stackTrace[0].lineNumber}"
                } else {
                    "$response with ${cause.javaClass.simpleName}: ${cause.message} on line ${cause.stackTrace[0].lineNumber}"
                }
            }
            result != null -> {
                "$response , result = $result"
            }
            else -> response
        }

        event.reply(message).setEphemeral(true).queue()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    object EvalVars {
        lateinit var user: User
        lateinit var member: Member
        lateinit var channel: TextChannel
        lateinit var guild: Guild
        lateinit var config: Config

        val botMember get() = guild.selfMember
        val botUser get() = botMember.user
    }
}
