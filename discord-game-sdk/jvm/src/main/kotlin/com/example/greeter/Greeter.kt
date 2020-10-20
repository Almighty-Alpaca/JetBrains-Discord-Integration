package com.example.greeter

import gamesdk.impl.utils.NativeLoader

class Greeter {
    companion object {
        init {
            NativeLoader.loadLibraries(Greeter::class.java.classLoader, "discord_game_sdk", "discord_game_sdk_cpp", "discord_game_sdk_kotlin")
        }
    }

    external fun sayHello(name: String?): String?
}
