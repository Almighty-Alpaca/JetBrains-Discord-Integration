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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.get
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.User
import java.awt.*
import java.awt.Color
import java.awt.geom.Path2D
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO

object Color {
    val whiteTranslucent60 = Color(1F, 1F, 1F, 0.6F)
    val whiteTranslucent80 = Color(1F, 1F, 1F, 0.8F)
    val blurple = Color(114, 137, 218)
    val darkOverlay = Color(0.0F, 0.0F, 0.0F, 0.05F)
    val green = Color(67, 181, 129)
    val greenTranslucent = Color(180, 255, 205, 154)
}

object Roboto {
    val regular: Font = Font.createFont(Font.TRUETYPE_FONT, Roboto::class.java.getResourceAsStream("/discord/fonts/roboto/Roboto-Regular.ttf"))
    val medium: Font = Font.createFont(Font.TRUETYPE_FONT, Roboto::class.java.getResourceAsStream("/discord/fonts/roboto/Roboto-Medium.ttf"))
    val bold: Font = Font.createFont(Font.TRUETYPE_FONT, Roboto::class.java.getResourceAsStream("/discord/fonts/roboto/Roboto-Bold.ttf"))
    val black: Font = Font.createFont(Font.TRUETYPE_FONT, Roboto::class.java.getResourceAsStream("/discord/fonts/roboto/Roboto-Black.ttf"))
}

fun BufferedImage.toScaledImage(width: Int, height: Int = width, hints: Int = Image.SCALE_SMOOTH): BufferedImage = createImage(width, height).withGraphics {
    drawImage(getScaledInstance(width, height, hints), 0, 0, null)
}

fun BufferedImage.toRoundImage(): BufferedImage = createImage(width, height).withGraphics {
    color = Color(0, 0, 0, 0)
    fillRect(0, 0, width, height)

    setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

    composite = AlphaComposite.Src
    color = Color.white
    fillArc(0, 0, width, height, 0, 360)

    composite = AlphaComposite.SrcIn
    drawImage(this@toRoundImage, 0, 0, null)
}

fun BufferedImage.withRoundedCorners(radius: Double): BufferedImage = createImage(width, height).withGraphics g2d@{
    //    this@g2d.color = Color(0, 0, 0, 0)
//    this@g2d.fillRect(0, 0, width, height)

    setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

    composite = AlphaComposite.Src
    color = Color.white
    fill(RoundRectangle2D.Double(0.0, 0.0, width.toDouble(), height.toDouble(), radius, radius))

    composite = AlphaComposite.SrcIn
    drawImage(this@withRoundedCorners, 0, 0, null)
}

fun getAvatar(user: User, size: Int): Image? {
    val raw = user.getAvatar(size)
    val scaled = raw?.toScaledImage(size)
    return scaled?.toRoundImage()
}

fun URL.getImage(): BufferedImage? = get { stream -> ImageIO.read(stream) }

inline fun BufferedImage.withGraphics(block: Graphics2D.() -> Unit): BufferedImage = with(createGraphics()) {
    setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

    block()

    dispose()

    return this@withGraphics
}

// TODO: fix image size
// @Suppress("UndesirableClassUsage")
// fun createImage(width: Int, height: Int) = try {
//     UIUtil.createImage(width, height, BufferedImage.TYPE_INT_ARGB)
// } catch (e: NoClassDefFoundError) {
//     BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
// }

@Suppress("UndesirableClassUsage")
fun createImage(width: Int, height: Int) = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

inline fun Graphics2D.withTranslation(x: Number, y: Number, block: () -> Unit) {
    translate(x.toInt(), y.toInt())
    block()
    translate(-x.toInt(), -y.toInt())
}

fun roundRectangle(
    x: Double,
    y: Double,
    width: Double,
    height: Double,
    radiusTopLeft: Double = 0.0,
    radiusTopRight: Double = 0.0,
    radiusBottomRight: Double = 0.0,
    radiusBottomLeft: Double = 0.0
) = Path2D.Double().apply {
    moveTo(x + radiusTopLeft, y)
    lineTo(x + width - radiusTopRight, y)
    curveTo(x + width, y, x + width, y, x + width, y + radiusTopRight)
    lineTo(x + width, y + height - radiusBottomRight)
    curveTo(x + width, y + height, x + width, y + height, x + width - radiusBottomRight, y + height)
    lineTo(x + radiusBottomLeft, y + height)
    curveTo(x, y + height, x, y + height, x, y + height - radiusBottomLeft)
    lineTo(x, y + radiusTopLeft)
    curveTo(x, y, x, y, x + radiusTopLeft, y)
}
