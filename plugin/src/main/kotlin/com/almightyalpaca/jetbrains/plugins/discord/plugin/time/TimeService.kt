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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.time

import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.Renderer
import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.renderService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.intellij.ide.IdeEventQueue
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolder
import com.intellij.openapi.vfs.VirtualFile
import java.util.concurrent.atomic.AtomicBoolean

private val TIME_OPENED = Key.create<Long>("discord.time.started")
//private val TIME_ELAPSED = Key.create<Long>("discord.time.elapsed")
//private val TIME_FOCUS_GAINED = Key.create<Long>("discord.time.focus.gained")

var Application.timeStarted
    get() = startTime
    //        getUserData(TIME_OPENED) ?: System.currentTimeMillis()
    private set(value) {
//        putUserData(TIME_OPENED, value)
    }

var Project.timeOpened
    get() = getUserData(TIME_OPENED) ?: System.currentTimeMillis()
    private set(value) {
        putUserData(TIME_OPENED, value)
    }

var VirtualFile.timeOpened
    get() = getUserData(TIME_OPENED) ?: System.currentTimeMillis()
    private set(value) {
        putUserData(TIME_OPENED, value)
    }

//var Application.focusGainedAt
//    get() = getUserData(TIME_STARTED) ?: System.currentTimeMillis()
//    private set(value) {
//        putUserData(TIME_STARTED, value)
//    }
//
//fun getTimeElapsed() {
//    val service = timeoutService
//    if (service.status == Status.None) {
//
//    }
//}
//
//fun getTimeElapsed(project: Project) {
//
//}

val timeService: TimeoutService
    get() = service()

@Service
class TimeoutService : Disposable {
//    internal var status: Status = Status.None
//        private set

    private val loaded = AtomicBoolean(false)

    var idle = false
        private set

    private val idleListener = Runnable {
        idle = true

        renderService.render(true)
    }

    private val activityListener = Runnable {
        if (idle) {
            idle = false

            renderService.render(true)
        }
    }

//    private val propertyChangeListener = PropertyChangeListener { event ->
//        val oldFrame = event.oldValue
//        if (oldFrame is IdeFrame) {
//            val project = oldFrame.project
//
//            if (project != null) {
//
//            }
//        }
//
//        val newFrame = event.oldValue
//        if (newFrame is IdeFrame) {
//            val project = newFrame.project
//
//            if (project != null) {
//
//            }
//        }
//    }

    init {
//        KeyboardFocusManager.getCurrentKeyboardFocusManager()
//            .addPropertyChangeListener("focusedWindow", propertyChangeListener)

        IdeEventQueue.getInstance().addActivityListener(activityListener, this)
    }

    fun load() {
        if (Disposer.isDisposed(this)) {
            return
        }

        if (loaded.getAndSet(true)) {
            unload()
        }

        initializeApplication(ApplicationManager.getApplication())

        for (project in ProjectManager.getInstance().openProjects) {
            initializeProject(project)

            for (file in FileEditorManager.getInstance(project).openFiles) {
                initializeFile(file)
            }
        }

        val timeoutMillis = settings.timeoutMinutes.get(Renderer.Mode.NORMAL) * 60 * 1000
        IdeEventQueue.getInstance().addIdleListener(idleListener, timeoutMillis)
    }

    private fun unload() {
        IdeEventQueue.getInstance().removeIdleListener(idleListener)
    }

    override fun dispose() {
        unload()

//        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener(propertyChangeListener)
    }

    fun initializeApplication(application: Application) = Unit
    fun initializeProject(project: Project) = initialize(project)
    fun initializeFile(file: VirtualFile) = initialize(file)

    private fun initialize(holder: UserDataHolder) {
        if (holder.getUserData(TIME_OPENED) == null) {
            holder.putUserData(TIME_OPENED, System.currentTimeMillis())
        }
    }
}

internal sealed class Status {
    object None : Status()
    class Application(val lastChange: Long = System.currentTimeMillis()) : Status()
    class Project(val lastChange: Long = System.currentTimeMillis(), project: com.intellij.openapi.project.Project) :
        Status()
}
