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

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandEvent
import com.uchuhimo.konf.Config
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import javax.script.ScriptEngineManager
import javax.script.ScriptException

private const val imports = """
    import com.almightyalpaca.discord.bot.jetbrains.commands.EvalCommand.EvalVars.self
    import com.almightyalpaca.discord.bot.jetbrains.commands.EvalCommand.EvalVars.bot
    import com.almightyalpaca.discord.bot.jetbrains.commands.EvalCommand.EvalVars.channel
    import com.almightyalpaca.discord.bot.jetbrains.commands.EvalCommand.EvalVars.guild
    import com.almightyalpaca.discord.bot.jetbrains.commands.EvalCommand.EvalVars.client
    import com.almightyalpaca.discord.bot.jetbrains.commands.EvalCommand.EvalVars.config
    """

class EvalCommand(private val config: Config) : Command() {
    init {
        name = "eval"
        hidden = true
        guildOnly = true
        ownerCommand = true
    }

    private val manager = ScriptEngineManager()

    override fun execute(event: CommandEvent) {
        val engine = manager.getEngineByExtension("kts")

        with(EvalVars) {
            self = event.member
            bot = event.selfMember
            channel = event.textChannel
            guild = event.guild
            client = event.client
            config = this@EvalCommand.config
        }

        val startTime = System.nanoTime()

        val result = try {
            engine.eval(imports + event.args)
        } catch (e: ScriptException) {
            e
        }

        val endTime = System.nanoTime()
        val timeUsed = endTime - startTime

        val response = "Executed in ${timeUsed}ns"
        if (result is Exception) {
            result.printStackTrace()

            val cause = result.cause
            if (cause == null)
                event.replyError("$response with ${result.javaClass.simpleName}: ${result.message} on line ${result.stackTrace[0].lineNumber}")
            else
                event.replyError("$response with ${cause.javaClass.simpleName}: ${cause.message} on line ${cause.stackTrace[0].lineNumber}")
        } else if (result != null)
            event.replySuccess("$response , result = $result")
    }

    object EvalVars {
        lateinit var self: Member
        lateinit var bot: Member
        lateinit var channel: TextChannel
        lateinit var guild: Guild
        lateinit var client: CommandClient
        var config: Config = Config()
    }
}
