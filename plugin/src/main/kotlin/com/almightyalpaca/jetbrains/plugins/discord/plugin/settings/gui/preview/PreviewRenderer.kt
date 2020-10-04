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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.gui.preview

import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.Data
import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.dataService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.RenderContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.Renderer
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresence
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.rpcService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.source.sourceService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.time.timeActive
import com.almightyalpaca.jetbrains.plugins.discord.plugin.time.timeOpened
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.*
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Color.blurple
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Color.darkOverlay
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Color.green
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Color.greenTranslucent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Color.whiteTranslucent60
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Color.whiteTranslucent80
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.project.ProjectManager
import org.apache.commons.lang3.time.DurationFormatUtils
import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.awt.image.BufferedImage
import java.time.Duration
import java.time.OffsetDateTime
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.User as RPCUser

class PreviewRenderer {
    private val user = User()
    private val game = Game()

    private val width = 250
    private val height = 273

    private var image: BufferedImage = createImage(width, height)

    val dummy by lazy lazy@{
        val image = createImage(width, height)

        image.withGraphics {
            color = Color(0, 0, 0, 0)
            fillRect(0, 0, image.width, image.height)
        }

        return@lazy image
    }

    private val font16Bold: Font = Roboto.bold.deriveFont(16F)
    private val font16Regular: Font = Roboto.regular.deriveFont(16F)
    private val font14Bold: Font = Roboto.bold.deriveFont(14F)
    private val font14Medium: Font = Roboto.medium.deriveFont(13F)
    private val font11Black: Font = Roboto.black.deriveFont(11F)

    private val font16BoldMetrics: FontMetrics = image.graphics.getFontMetrics(font16Bold)
    private val font16RegularMetrics: FontMetrics = image.graphics.getFontMetrics(font16Regular)
    private val font14BoldMetrics: FontMetrics = image.graphics.getFontMetrics(font14Bold)
    private val font14MediumMetrics: FontMetrics = image.graphics.getFontMetrics(font14Medium)
    private val font11BlackMetrics: FontMetrics = image.graphics.getFontMetrics(font11Black)

    private val font11BlackHeight: Int = font11BlackMetrics.height

    private val font16BoldBaseline: Int = font16BoldMetrics.maxAscent + font16BoldMetrics.leading
    private val font16RegularBaseline: Int = font16RegularMetrics.maxAscent + font16RegularMetrics.leading
    private val font14BoldBaseline: Int = font14BoldMetrics.maxAscent + font14BoldMetrics.leading
    private val font14MediumBaseline: Int = font14MediumMetrics.maxAscent + font14MediumMetrics.leading
    private val font11BlackBaseline: Int = font11BlackMetrics.maxAscent + font11BlackMetrics.leading

    private val font14BoldMaxHeight: Int = font14BoldMetrics.maxAscent + font14BoldMetrics.leading + font14BoldMetrics.maxDescent
    private val font14MediumMaxHeight: Int = font14MediumMetrics.maxAscent + font14MediumMetrics.leading + font14MediumMetrics.maxDescent

    @Synchronized
    suspend fun draw(type: Renderer.Type.Application, force: Boolean = false): ModifiedImage {
        val data = dataService.getData(Renderer.Mode.PREVIEW)?.completeMissingData() ?: return ModifiedImage(false, image)

        val context = RenderContext(sourceService.source, data, Renderer.Mode.PREVIEW)
        val renderer = type.createRenderer(context)
        val presence = renderer.render()

        val modified = user.draw(image, force) or game.draw(image, presence, force)

        return ModifiedImage(modified, image)
    }

    private inner class User {
        private var lastUser: RPCUser? = null
        private var lastAvatarEmpty = true

        fun draw(image: BufferedImage, force: Boolean): Boolean {
            val user = rpcService.user

            val avatar = when {
                user != lastUser -> {
                    lastAvatarEmpty = true
                    getAvatar(user, 90)
                }
                lastAvatarEmpty -> getAvatar(user, 90)
                else -> null
            }

            var modified = false
            if (force || user != lastUser || (lastAvatarEmpty && avatar != null)) {
                modified = true

                image.withGraphics {
                    color = blurple
                    fill(roundRectangle(0.0, 0.0, 250.0, image.height * 0.6, 10.0, 10.0))

                    withTranslation(10, 20) {
                        val mid = (image.width - 10) / 2

                        // Avatar
                        color = Color.red
                        drawImage(avatar, mid - 45, 0, null)

                        // Online indicator
                        color = blurple
                        fillArc(mid - 45 + 65, 65, 26, 26, 0, 360)

                        color = greenTranslucent
                        fillArc(mid - 45 + 68, 68, 20, 20, 0, 360)

                        color = green
                        fillArc(mid - 45 + 70, 70, 16, 16, 0, 360)

                        val name = user.name
                        val tag = user.tag?.let { tag -> "#" + tag.padStart(4, '0') } ?: ""
                        val nameWidth = font16BoldMetrics.stringWidth(name)
                        val tagWidth = font16RegularMetrics.stringWidth(tag)

                        val textWidth = nameWidth + tagWidth

                        color = Color.white
                        font = font16Bold
                        drawString(name, mid - textWidth / 2, 100 + font16BoldBaseline)

                        color = whiteTranslucent60
                        font = font16Regular
                        drawString(tag, mid - textWidth / 2 + nameWidth, 100 + font16BoldBaseline + (font16BoldBaseline - font16RegularBaseline) / 2)
                    }
                }

                if (avatar != null) {
                    lastAvatarEmpty = false
                }

                lastUser = user
            }

            return modified
        }
    }

    private inner class Game {
        private val images = Images()
        private val text = Text()

        var first = true
        var lastApplicationName: String? = null
        var lastImagesEmpty: Boolean? = null

        fun draw(image: BufferedImage, presence: RichPresence, force: Boolean): Boolean {

            val (imagesModified, imagesEmpty) = images.draw(image, presence, force)

            if (force || first) {

                // "Playing a game"
                image.withGraphics {
                    val sectionStart = (image.height * 0.6).toInt()

                    color = blurple
                    fillRect(0, sectionStart, image.width, 10 + font11BlackHeight + 8)
                    color = darkOverlay
                    fillRect(0, sectionStart, image.width, 10 + font11BlackHeight + 8)

                    font = font11Black
                    color = Color.white
                    drawString("PLAYING A GAME", 10, sectionStart + 10 + font11BlackBaseline)
                }
            }

            val applicationName = settings.applicationType.getPreviewValue().applicationNameReadable
            if (force || lastApplicationName != applicationName || lastImagesEmpty != imagesEmpty) {
                // IDE name
                image.withGraphics {
                    val sectionStart = (image.height * 0.6).toInt() + 10 + font11BlackHeight + 8
                    val indentation = when (imagesEmpty) {
                        true -> 7
                        false -> 77
                    }

                    color = blurple
                    fillRect(indentation, sectionStart, image.width - indentation, font14BoldMaxHeight)
                    color = darkOverlay
                    fillRect(indentation, sectionStart, image.width - indentation, font14BoldMaxHeight)

                    font = font14Bold
                    color = whiteTranslucent80
                    drawString(applicationName, indentation + 3, sectionStart + font14BoldBaseline)
                }
            }

            val textModified = text.draw(image, presence, imagesEmpty, force)

            first = false
            lastApplicationName = applicationName
            lastImagesEmpty = imagesEmpty

            return imagesModified || textModified
        }

        private inner class Images {
            private var lastLarge: BufferedImage? = null
            private var lastLargeKey: String? = null
            private var lastSmall: BufferedImage? = null
            private var lastAppId: Long? = null

            fun draw(image: BufferedImage, presence: RichPresence, force: Boolean): Pair<Boolean, Boolean> {
                val largeKey = presence.largeImage?.key
                val smallKey = presence.smallImage?.key
                val appId = presence.appId

                if (force || lastLargeKey != largeKey || lastSmallKey != smallKey || lastAppId != appId) {
                    val large = if (lastLargeKey != largeKey || lastAppId != appId) {
                        presence.largeImage?.asset?.getImage(60)?.toScaledImage(60)?.withRoundedCorners(8.0)
                    } else {
                        lastLarge
                    }
                    val small = if (lastSmallKey != smallKey || lastAppId != appId) {
                        presence.smallImage?.asset?.getImage(20)?.toScaledImage(20)?.toRoundImage()
                    } else {
                        lastSmall
                    }

                    lastLarge = large
                    lastLargeKey = largeKey
                    lastSmall = small
                    lastSmallKey = smallKey
                    lastAppId = appId

                    image.withGraphics {
                        val sectionStart = (image.height * 0.6).toInt() + 10 + font11BlackHeight + 8
                        val width = when (large) {
                            null -> 8.0
                            else -> 78.0
                        }

                        color = blurple
                        fill(roundRectangle(0.0, sectionStart.toDouble(), width, (image.height - sectionStart).toDouble(), radiusBottomLeft = 10.0))
                        color = darkOverlay
                        fill(roundRectangle(0.0, sectionStart.toDouble(), width, (image.height - sectionStart).toDouble(), radiusBottomLeft = 10.0))

                        if (large != null) {
                            drawImage(large, 10, sectionStart, null)

                            if (small != null) {
                                color = blurple
                                fillArc(10 + 45 - 2, sectionStart + 45 - 2, 24, 24, 0, 360)
                                color = darkOverlay
                                fillArc(10 + 45 - 2, sectionStart + 45 - 2, 24, 24, 0, 360)

                                drawImage(small, 10 + 45, sectionStart + 45, null)
                            }

                            return true to false
                        }

                        return true to true
                    }

                }

                return false to (lastLarge == null)
            }

            private var lastSmallKey: String? = null
        }

        private inner class Text {
            private val details = Details()
            private val state = State()
            private val time = Time()

            var lastDetailsEmpty: Boolean? = null
            var lastStateEmpty: Boolean? = null

            fun draw(image: BufferedImage, presence: RichPresence, imagesEmpty: Boolean, force: Boolean): Boolean {
                val (detailsModified, detailsEmpty) = details.draw(image, presence, imagesEmpty, force)
                val (stateModified, stateEmpty) = state.draw(image, presence, imagesEmpty, detailsEmpty, force)
                val timeModified = time.draw(image, presence, imagesEmpty, detailsEmpty, stateEmpty, force)

                lastDetailsEmpty = detailsEmpty
                lastStateEmpty = stateEmpty

                return detailsModified || stateModified || timeModified
            }

            private inner class Details {
                var lastLine: String? = null

                fun draw(image: BufferedImage, presence: RichPresence, imagesEmpty: Boolean, force: Boolean): Pair<Boolean, Boolean> {
                    val line = presence.details

                    if (force || lastImagesEmpty != imagesEmpty || lastLine != line) {
                        lastLine = line

                        image.withGraphics {
                            val sectionStart = (image.height * 0.6).toInt() + 10 + font11BlackHeight + 8 + font14BoldMaxHeight

                            val indentation = when (imagesEmpty) {
                                true -> 7
                                false -> 77
                            }

                            color = blurple
                            fillRect(indentation, sectionStart, image.width - indentation, font14MediumMaxHeight)
                            color = darkOverlay
                            fillRect(indentation, sectionStart, image.width - indentation, font14MediumMaxHeight)

                            return if (line?.isInvisible() != false) {
                                true to true
                            } else {
                                val lineCut = line.limitWidth(font14MediumMetrics, image.width - (indentation + 3 + 10))

                                font = font14Medium
                                color = whiteTranslucent80
                                drawString(lineCut, indentation + 3, sectionStart + font14MediumBaseline)

                                true to false
                            }
                        }

                        return true to (line?.isInvisible() != false)
                    }

                    return false to (lastLine?.isInvisible() != false)
                }
            }

            private inner class State {
                var lastLine: String? = null

                fun draw(image: BufferedImage, presence: RichPresence, imagesEmpty: Boolean, detailsEmpty: Boolean, force: Boolean): Pair<Boolean, Boolean> {
                    val line = presence.state

                    if (force || lastImagesEmpty != imagesEmpty || lastDetailsEmpty != detailsEmpty || lastLine != line) {
                        lastLine = line

                        image.withGraphics {
                            var sectionStart = (image.height * 0.6).toInt() + 10 + font11BlackHeight + 8 + font14BoldMaxHeight
                            if (!detailsEmpty) {
                                sectionStart += font14MediumMaxHeight
                            }

                            val indentation = when (imagesEmpty) {
                                true -> 7
                                false -> 77
                            }

                            color = blurple
                            fillRect(indentation, sectionStart, image.width - indentation, font14MediumMaxHeight)
                            color = darkOverlay
                            fillRect(indentation, sectionStart, image.width - indentation, font14MediumMaxHeight)

                            return if (line?.isInvisible() != false) {
                                true to true
                            } else {
                                val lineCut = line.limitWidth(font14MediumMetrics, image.width - (indentation + 3 + 10))

                                font = font14Medium
                                color = whiteTranslucent80
                                drawString(lineCut, indentation + 3, sectionStart + font14MediumBaseline)

                                true to false
                            }
                        }
                    }

                    return true to (line?.isInvisible() != false)
                }
            }

            private inner class Time {
                var lastTime: OffsetDateTime? = null
                var lastTimeNow: OffsetDateTime? = null

                fun draw(
                    image: BufferedImage,
                    presence: RichPresence,
                    imagesEmpty: Boolean,
                    detailsEmpty: Boolean,
                    stateEmpty: Boolean,
                    force: Boolean
                ): Boolean {
                    val time = presence.startTimestamp
                    val timeNow = OffsetDateTime.now()

                    if (force || lastTime != time || lastTimeNow != timeNow || lastImagesEmpty != imagesEmpty || lastDetailsEmpty != detailsEmpty || lastStateEmpty != stateEmpty) {
                        lastTime = time
                        lastTimeNow = timeNow

                        image.withGraphics {
                            var sectionStart = (image.height * 0.6).toInt() + 10 + font11BlackHeight + 8 + font14BoldMaxHeight

                            if (!detailsEmpty) {
                                sectionStart += font14MediumMaxHeight
                            }

                            if (!stateEmpty) {
                                sectionStart += font14MediumMaxHeight
                            }

                            val indentation = when (imagesEmpty) {
                                true -> 7.0
                                false -> 77.0
                            }

                            color = blurple
                            fill(roundRectangle(indentation, sectionStart.toDouble(), image.width - indentation, (image.height - sectionStart).toDouble(), radiusBottomRight = 10.0))
                            color = darkOverlay
                            fill(roundRectangle(indentation, sectionStart.toDouble(), image.width - indentation, (image.height - sectionStart).toDouble(), radiusBottomRight = 10.0))

                            if (time != null) {
                                val millis = Duration.between(time, timeNow).toMillis()
                                val formatted = when {
                                    millis < 1 * 60 * 60 * 1000 -> DurationFormatUtils.formatDuration(millis, "mm:ss")
                                    else -> DurationFormatUtils.formatDuration(millis, "HH:mm:ss")
                                }

                                font = font14Medium
                                color = whiteTranslucent80
                                drawString("$formatted elapsed", indentation.toInt() + 3, sectionStart + font14MediumBaseline)
                            }
                        }

                        return true
                    }

                    return false
                }
            }
        }
    }
}

private val applicationCode = ApplicationInfo.getInstance().build.productCode

private fun Data.completeMissingData(): Data.File {
    val application = this as? Data.Application
    val project = this as? Data.Project
    val file = this as? Data.File

    val projectDescription = project?.projectDescription

    val dummyFileName = sourceService.source.getApplicationsOrNull()?.get(applicationCode)?.dummyFile ?: "dummy.txt"

    val applicationTimeOpened = application?.applicationTimeOpened ?: ApplicationManager.getApplication().timeOpened
    val applicationTimeActive = application?.applicationTimeActive ?: ApplicationManager.getApplication().timeActive

    return Data.File(
        application?.applicationName ?: settings.applicationType.getPreviewValue().applicationNameReadable,
        application?.applicationVersion ?: ApplicationInfoEx.getInstance().fullVersion,
        applicationTimeOpened,
        applicationTimeActive,
        application?.applicationSettings ?: settings,
        project?.projectName ?: "Dummy project",
        if (projectDescription.isNullOrBlank()) "Dummies are very nice test objects" else projectDescription,
        project?.projectTimeOpened ?: applicationTimeOpened,
        project?.projectTimeActive ?: applicationTimeActive,
        project?.projectSettings ?: ProjectManager.getInstance().defaultProject.settings,
        project?.vcsBranch ?: "master",
        project?.debuggerActive ?: false,
        file?.fileName ?: dummyFileName,
        file?.fileNameUnique ?: dummyFileName,
        file?.fileTimeOpened ?: project?.projectTimeOpened ?: applicationTimeOpened,
        file?.fileTimeActive ?: project?.projectTimeActive ?: applicationTimeActive,
        file?.filePath ?: "dummy/$dummyFileName",
        file?.fileIsWriteable ?: true,
        file?.editorIsTextEditor ?: false,
        file?.caretLine ?: 0,
        file?.lineCount ?: 0,
        file?.moduleName ?: "dummy-module",
        file?.pathInModule ?: "/dummy/$dummyFileName",
        file?.fileSize ?: 0
    )
}
