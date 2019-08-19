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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.logging

// TODO: proper logging
abstract class Logger {
    var enabled: Boolean = System.getenv("com.almightyalpaca.jetbrains.plugins.discord.plugin.logging") == "true"

    abstract fun log(level: Level, o: Any?)
    abstract fun log(level: Level, t: Throwable, o: Any?)

    class Impl : Logger() {
        override fun log(level: Level, o: Any?) = println(o)
        override fun log(level: Level, t: Throwable, o: Any?) {
            log(level, o)
            t.printStackTrace()
        }
    }

    enum class Level {
        TRACE, DEBUG, INFO, WARN, ERROR
    }
}
