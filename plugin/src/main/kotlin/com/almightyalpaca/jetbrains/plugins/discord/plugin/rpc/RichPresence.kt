package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc

import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.getImage
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.limit
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.roundToNextPowerOfTwo
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Asset
import java.awt.image.BufferedImage
import java.net.URL
import java.time.OffsetDateTime
import javax.imageio.ImageIO

class RichPresence(
    val appId: Long?,
    state: String? = null,
    details: String? = null,
    var startTimestamp: OffsetDateTime? = null,
    var endTimestamp: OffsetDateTime? = null,
    var largeImage: Image? = null,
    var smallImage: Image? = null,
    partyId: String? = null,
    var partySize: Int = 0,
    var partyMax: Int = 0,
    matchSecret: String? = null,
    joinSecret: String? = null,
    spectateSecret: String? = null,
    var instance: Boolean = false
) {
    constructor(appId: Long?, initializer: RichPresence.() -> Unit) : this(appId) {
        initializer()
    }

    var state = state?.limit(128, true)
        set(value) {
            field = value?.limit(128, true)
        }
    var details = details?.limit(128, true)
        set(value) {
            field = value?.limit(128, true)
        }

    var partyId = partyId?.limit(128)
        set(value) {
            field = value?.limit(128)
        }

    var matchSecret = matchSecret?.limit(128)
        set(value) {
            field = value?.limit(128)
        }
    var joinSecret = joinSecret?.limit(128)
        set(value) {
            field = value?.limit(128)
        }
    var spectateSecret = spectateSecret?.limit(128)
        set(value) {
            field = value?.limit(128)
        }

    class Image(var asset: Asset?, text: String?) {
        val key = asset?.id?.limit(32, true)

        var text = text?.limit(128, true)
            set(value) {
                field = value?.limit(128, true)
            }
    }
}

sealed class User {
    abstract val name: String
    abstract val tag: String?
    abstract fun getAvatar(size: Int? = null): BufferedImage?

    class Normal(override val name: String, override val tag: String, val id: Long, val avatarId: String) : User() {
        override fun getAvatar(size: Int?) = when (size) {
            null -> URL("https://cdn.discordapp.com/avatars/$id/$avatarId.png?size=128")
            else -> URL("https://cdn.discordapp.com/avatars/$id/$avatarId.png?size=${size.roundToNextPowerOfTwo().coerceIn(16..4096)}")
        }.getImage()
    }

    object CLYDE : User() {
        override val name = "Clyde"
        override val tag: String? = null

        // URL: https://discordapp.com/assets/f78426a064bc9dd24847519259bc42af.png
        override fun getAvatar(size: Int?): BufferedImage = ImageIO.read(CLYDE::class.java.getResourceAsStream("/images/avatars/clyde.png"))
    }

    companion object
}
