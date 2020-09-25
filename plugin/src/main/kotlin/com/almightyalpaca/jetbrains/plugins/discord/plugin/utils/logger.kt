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

@file:Suppress("unused")

package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

import com.intellij.openapi.diagnostic.Logger

inline fun Logger.traceLazy(lazyMessage: () -> String) {
    if (isTraceEnabled) {
        info(lazyMessage())
    }
}

inline fun Logger.traceLazy(t: Throwable, lazyMessage: () -> String) {
    if (isTraceEnabled) {
        info(lazyMessage(), t)
    }
}

inline fun Logger.debugLazy(lazyMessage: () -> String) {
    if (isDebugEnabled) {
        debug(lazyMessage())
    }
}

inline fun Logger.debugLazy(t: Throwable, lazyMessage: () -> String) {
    if (isDebugEnabled) {
        debug(lazyMessage() as String?, t)
    }
}

inline fun Logger.infoLazy(lazyMessage: () -> String) {
    info(lazyMessage())
}

inline fun Logger.infoLazy(t: Throwable, lazyMessage: () -> String) {
    info(lazyMessage(), t)
}

inline fun Logger.warnLazy(lazyMessage: () -> String) {
    warn(lazyMessage())
}

inline fun Logger.warnLazy(t: Throwable, lazyMessage: () -> String) {
    warn(lazyMessage(), t)
}

inline fun Logger.errorLazy(lazyMessage: () -> String) {
    error(lazyMessage())
}

inline fun Logger.errorLazy(t: Throwable, lazyMessage: () -> String) {
    error(lazyMessage(), t)
}
