package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.User
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.get
import java.awt.*
import java.awt.Color
import java.awt.geom.Path2D
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
    val regular: Font = Font.createFont(Font.TRUETYPE_FONT, Roboto::class.java.getResourceAsStream("/fonts/roboto/Roboto-Regular.ttf"))
    val medium: Font = Font.createFont(Font.TRUETYPE_FONT, Roboto::class.java.getResourceAsStream("/fonts/roboto/Roboto-Medium.ttf"))
    val bold: Font = Font.createFont(Font.TRUETYPE_FONT, Roboto::class.java.getResourceAsStream("/fonts/roboto/Roboto-Bold.ttf"))
    val black: Font = Font.createFont(Font.TRUETYPE_FONT, Roboto::class.java.getResourceAsStream("/fonts/roboto/Roboto-Black.ttf"))

}

fun BufferedImage.toScaledImage(width: Int, height: Int = width, hints: Int = Image.SCALE_SMOOTH): BufferedImage {
    val image = createImage(width, height)

    image.withGraphics {
        drawImage(getScaledInstance(width, height, hints), 0, 0, null)
    }

    return image
}

fun BufferedImage.toRoundImage(): Image {
    val image = createImage(width, height)

    image.withGraphics {
        color = Color(0, 0, 0, 0)
        fillRect(0, 0, width, height)

        setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        composite = AlphaComposite.Src
        color = Color.white
        fillArc(0, 0, width, height, 0, 360)

        composite = AlphaComposite.SrcAtop
        drawImage(this@toRoundImage, 0, 0, null)
    }

    return image
}

fun getAvatar(user: User, size: Int): Image? {
    val raw = user.getAvatar(size)
    val scaled = raw?.toScaledImage(size)
    return scaled?.toRoundImage()
}

fun URL.getImage(): BufferedImage? = get { stream -> ImageIO.read(stream) }

inline fun BufferedImage.withGraphics(block: Graphics2D.() -> Unit) = with(createGraphics()) {
    setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

    block()

    dispose()
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
