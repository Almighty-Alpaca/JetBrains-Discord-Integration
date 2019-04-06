package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc

import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.limit
import java.net.URL
import java.time.OffsetDateTime

class RichPresence(
        val appId: Long,
        state: String? = null,
        details: String? = null,
        var startTimestamp: OffsetDateTime? = null,
        var endTimestamp: OffsetDateTime? = null,
        var largeImage: Icon? = null,
        var smallImage: Icon? = null,
        partyId: String? = null,
        var partySize: Int = 0,
        var partyMax: Int = 0,
        matchSecret: String? = null,
        joinSecret: String? = null,
        spectateSecret: String? = null,
        var instance: Boolean = false
) {
    constructor(appId: Long, initializer: RichPresence.() -> Unit) : this(appId) {
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

    class Icon(key: String, text: String?) {
        var key = key.limit(32, true)
            set(value) {
                field = value.limit(32, true)
            }

        var text = text?.limit(128, true)
            set(value) {
                field = value?.limit(128, true)
            }
    }
}

sealed class User {
    abstract val name: String
    abstract val tag: String?
    abstract val avatarUrl: URL

    class Normal(override val name: String, override val tag: String, id: Long, avatarId: String) : User() {
        override val avatarUrl = URL("https://cdn.discordapp.com/avatars/$id/$avatarId.png?size=128")
    }

    class Clyde internal constructor() : User() {
        override val name = "Clyde"
        override val tag: String? = null
        override val avatarUrl = URL("https://discordapp.com/assets/f78426a064bc9dd24847519259bc42af.png")
    }

    companion object {
        val CLYDE = Clyde()
    }
}
