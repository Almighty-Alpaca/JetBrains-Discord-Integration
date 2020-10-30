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

package gamesdk.impl

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api.*
import gamesdk.api.*

internal class NativeCoreImpl private constructor(pointer: NativePointer) : NativeObjectImpl.Closeable(pointer), Core {
    override val applicationManager: ApplicationManager
        get() = TODO("Not yet implemented")
    override val userManager: UserManager
        get() = TODO("Not yet implemented")
    override val imageManager: ImageManager
        get() = TODO("Not yet implemented")

    override val activityManager: ActivityManager by nativeLazy { pointer -> NativeActivityManagerImpl(getActivityManager(pointer), this@NativeCoreImpl) }

    override val relationshipManager: RelationshipManager
        get() = TODO("Not yet implemented")
    override val lobbyManager: LobbyManager
        get() = TODO("Not yet implemented")
    override val networkManager: NetworkManager
        get() = TODO("Not yet implemented")
    override val overlayManager: OverlayManager
        get() = TODO("Not yet implemented")
    override val storageManager: StorageManager
        get() = TODO("Not yet implemented")
    override val storeManager: StoreManager
        get() = TODO("Not yet implemented")
    override val voiceManager: VoiceManager
        get() = TODO("Not yet implemented")
    override val achievementManager: AchievementManager
        get() = TODO("Not yet implemented")

    override fun runCallbacks(): DiscordResult = native { pointer -> runCallbacks(pointer).toDiscordResult() }
    override fun setLogHook(minLevel: DiscordLogLevel, hook: (level: DiscordLogLevel, message: String) -> Unit): Unit = TODO("Not yet implemented")

    override val destructor: NativeMethod<Unit> = Native::destroy

    companion object : NativeObjectCreator() {
        fun create(clientId: ClientId, createFlags: DiscordCreateFlags): Result<Core, DiscordResult> {
            return when (val result = native { create(clientId, createFlags.toNativeDiscordCreateFlags()) }) {
                is NativePointer -> Success(NativeCoreImpl(result))
                is NativeDiscordResult -> Failure(result.toDiscordResult())
                else -> throw IllegalStateException() // This should never happen unless the native method returns garbage
            }
        }
    }
}

/**
 * @return Either a [NativeDiscordResult] or a [NativePointer]
 */
private external fun Native.create(clientId: ClientId, createFlags: NativeDiscordCreateFlags): Any

private external fun Native.destroy(core: NativePointer)

private external fun Native.runCallbacks(core: NativePointer): Int

private external fun Native.getActivityManager(core: NativePointer): NativePointer
