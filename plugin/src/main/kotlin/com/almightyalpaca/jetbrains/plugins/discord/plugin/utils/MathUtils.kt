package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

fun Int.roundToNextPowerOfTwo(): Int {
    var v = this - 1

    v = v or (v shr 1)
    v = v or (v shr 2)
    v = v or (v shr 4)
    v = v or (v shr 8)
    v = v or (v shr 16)

    return v + 1
}