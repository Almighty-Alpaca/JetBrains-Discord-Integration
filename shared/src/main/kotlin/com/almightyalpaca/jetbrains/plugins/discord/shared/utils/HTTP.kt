package com.almightyalpaca.jetbrains.plugins.discord.shared.utils

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object HTTP {
    fun <T> get(url: URL, handler: (InputStream) -> T): T {
        val connection = (url.openConnection() as HttpURLConnection).apply {
            setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.67 Safari/537.36")
        }

        return connection.inputStream.use { inputStream ->
            handler(inputStream)
        }
    }
}