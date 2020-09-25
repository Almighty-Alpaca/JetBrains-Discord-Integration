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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.time

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
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.min

private val TIME_OPENED = Key.create<Long>("discord.time.opened")
private val TIME_IDLE = Key.create<Long>("discord.time.idle")

/**
 * Timestamp of when the application was opened
 */
var Application.timeOpened
    get() = getUserData(TIME_OPENED) ?: System.currentTimeMillis()
    private set(value) = putUserData(TIME_OPENED, value)

/**
 * Timestamp of when the project was opened
 */
var Project.timeOpened
    get() = getUserData(TIME_OPENED) ?: System.currentTimeMillis()
    private set(value) = putUserData(TIME_OPENED, value)

/**
 * Timestamp of when the file was opened
 */
var VirtualFile.timeOpened
    get() = getUserData(TIME_OPENED) ?: System.currentTimeMillis()
    private set(value) = putUserData(TIME_OPENED, value)

var Application.durationIdle
    get() = getUserData(TIME_IDLE) ?: 0
    private set(value) = putUserData(TIME_IDLE, value)

var Project.durationIdle
    get() = getUserData(TIME_IDLE) ?: 0
    private set(value) = putUserData(TIME_IDLE, value)

var VirtualFile.durationIdle
    get() = getUserData(TIME_IDLE) ?: 0
    private set(value) = putUserData(TIME_IDLE, value)

/**
 * Calculated timestamp of the start of the active time of the application.
 * Another way to express this would be `now() - activeDuration`
 */
val Application.timeActive
    get() = timeOpened + durationIdle

/**
 * Calculated timestamp of the start of the active time of the project.
 * Another way to express this would be `now() - activeDuration`
 */
val Project.timeActive
    get() = timeOpened + durationIdle

/**
 * Calculated timestamp of the start of the active time of the file.
 * Another way to express this would be `now() - activeDuration`
 */
val VirtualFile.timeActive
    get() = timeOpened + durationIdle

val timeService: TimeService
    get() = service()

/**
 * The TimeService keeps track of when the application/projects/files are opened
 * and when the IDE is idle.
 */
@Service
class TimeService : Disposable {
//    internal var status: Status = Status.None
//        private set

    private val loaded = AtomicBoolean(false)

    val idle
        get() = idleSince.get() != null

    /**
     * Timestamp of when the IDE went into idle mode.
     * Null means the IDE is currently active.
     */
    private val idleSince = AtomicReference<Long?>(null)

    private val idleListener = Runnable {
        idleSince.updateAndGet { old ->
            when (old) {
                null -> System.currentTimeMillis()
                else -> min(old, System.currentTimeMillis())
            }
        }

        renderService.render(true)
    }

    /**
     * This listener is called whenever the user interacts with the IDE.
     * **This is called very often, don't do too much stuff in here.**
     * Especially not outside the `if` statement.
     */
    private val activityListener = Runnable {
        val idleSince = idleSince.getAndSet(null)

        if (idleSince != null) {
            val idleDuration = System.currentTimeMillis() - idleSince

            if (settings.timeoutResetTimeEnabled.getStoredValue()) {
                val now = System.currentTimeMillis()
                val application = ApplicationManager.getApplication()
                application.durationIdle = 0
                application.timeOpened = now

                for (project in ProjectManager.getInstance().openProjects) {
                    project.durationIdle = 0
                    project.timeOpened = now

                    for (file in FileEditorManager.getInstance(project).openFiles) {
                        file.durationIdle = 0
                        file.timeOpened = now
                    }
                }
            } else {
                ApplicationManager.getApplication().durationIdle += idleDuration

                for (project in ProjectManager.getInstance().openProjects) {
                    project.durationIdle += idleDuration

                    for (file in FileEditorManager.getInstance(project).openFiles) {
                        file.durationIdle += idleDuration
                    }
                }
            }

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

        val timeoutMillis = settings.timeoutMinutes.getStoredValue() * 60 * 1000
        IdeEventQueue.getInstance().addIdleListener(idleListener, timeoutMillis)
    }

    private fun unload() {
        IdeEventQueue.getInstance().removeIdleListener(idleListener)
    }

    override fun dispose() {
        unload()

//        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener(propertyChangeListener)
    }

    fun initializeApplication(application: Application) = initialize(application)
    fun initializeProject(project: Project) = initialize(project)
    fun initializeFile(file: VirtualFile) = initialize(file)

    private fun initialize(holder: UserDataHolder) {
        if (holder.getUserData(TIME_OPENED) == null) {
            holder.putUserData(TIME_OPENED, System.currentTimeMillis())
        }
        if (holder.getUserData(TIME_IDLE) == null) {
            holder.putUserData(TIME_IDLE, 0)
        }
    }
}

//internal sealed class Status {
//    object None : Status()
//    class Application(val lastChange: Long = System.currentTimeMillis()) : Status()
//    class Project(val lastChange: Long = System.currentTimeMillis(), project: com.intellij.openapi.project.Project) :
//        Status()
//}
