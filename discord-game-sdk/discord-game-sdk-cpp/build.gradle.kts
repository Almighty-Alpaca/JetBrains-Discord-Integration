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

plugins {
    `cpp-library`
}

toolChains {
    withType<Gcc> {
        eachPlatform {
            cppCompiler.withArguments {
                add("-std=c++17")
            }
        }
    }

    withType<Clang> {
        eachPlatform {
            cppCompiler.withArguments {
                add("-std=c++17")
            }
        }
    }
}

library {
    baseName.set("discord_game_sdk_cpp")

    linkage.set(setOf(Linkage.SHARED))

    targetMachines.set(
        listOf(
            machines.windows.x86_64,
            machines.windows.x86,
            machines.linux.x86_64,
            machines.macOS.x86_64
        )
    )

    @Suppress("UnstableApiUsage")
    binaries.whenElementFinalized binary@{
        dependencies.apply {
            implementation(files(getSharedLibrary(targetMachine)))
        }
    }
}

fun getSharedLibrary(targetMachine: TargetMachine): String {
    return when (val os = targetMachine.operatingSystemFamily.name) {
        "windows" -> {
            when (val architecture = targetMachine.architecture.name) {
                MachineArchitecture.X86 -> "lib/discord_game_sdk/windows/x86/discord_game_sdk.dll"
                MachineArchitecture.X86_64 -> "lib/discord_game_sdk/windows/x86-64/discord_game_sdk.dll"
                else -> throw GradleException("Unknown architecture'${architecture}'.")
            }
        }
        "linux" -> "lib/discord_game_sdk/linux/x86-64/libdiscord_game_sdk.so"
        "os x" -> "lib/discord_game_sdk/macos/x86-64/libdiscord_game_sdk.dylib"
        else -> throw GradleException("Unknown operating system family '${os}'.")
    }
}
