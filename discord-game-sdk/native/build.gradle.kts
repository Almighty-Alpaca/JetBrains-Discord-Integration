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

import org.gradle.internal.jvm.Jvm
import org.gradle.internal.os.OperatingSystem

plugins {
    `cpp-library`
    `cpp-unit-test`
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
    baseName.set("discord_game_sdk_kotlin")

    linkage.set(setOf(Linkage.SHARED, Linkage.STATIC))

    targetMachines.set(
        listOf(
            machines.windows.x86_64,
            machines.windows.x86,
            machines.linux.x86_64,
            machines.macOS.x86_64
        )
    )

    dependencies {
        implementation(project(":discord-game-sdk:discord-game-sdk-cpp"))
    }

    privateHeaders {
        from(project(":discord-game-sdk:jvm").layout.buildDirectory.dir("generated/headers"))
        builtBy(":discord-game-sdk:jvm:generateJniHeaders")

        from(File(Jvm.current().javaHome, "include"))

        val os = OperatingSystem.current()
        when {
            os.isWindows -> from(File(Jvm.current().javaHome, "include/win32"))
            os.isMacOsX -> from(File(Jvm.current().javaHome, "include/darwin"))
            os.isLinux -> from(File(Jvm.current().javaHome, "include/linux"))
            else -> throw GradleException("Unknown operating system family '${os}'.")
        }
    }
}

unitTest {
    targetMachines.set(library.targetMachines)
}
