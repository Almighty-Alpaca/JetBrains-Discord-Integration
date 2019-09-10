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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.keys

import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.maxNullable
import com.intellij.openapi.application.Application
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile

private val keyOpenedAt: Key<Long> = Key.create("com.almightyalpaca.jetbrains.plugins.discord.plugin.keys.openedAt")
private val keyAccessedAt: Key<Long> = Key.create("com.almightyalpaca.jetbrains.plugins.discord.plugin.keys.accessedAt")

var VirtualFile.openedAt: Long
    get() = this.getUserData(keyOpenedAt)!!
    set(value) {
        this.putUserData(keyOpenedAt, value)
    }

var Project.openedAt: Long
    get() = this.getUserData(keyOpenedAt)!!
    set(value) {
        this.putUserData(keyOpenedAt, value)
    }

var Application.openedAt: Long
    get() = this.getUserData(keyOpenedAt)!!
    set(value) {
        this.putUserData(keyOpenedAt, value)
    }

var Application.accessedAt: Long
    get() = maxNullable(this.getUserData(keyAccessedAt), openedAt)
    set(value) {
        this.putUserData(keyAccessedAt, value)
    }
