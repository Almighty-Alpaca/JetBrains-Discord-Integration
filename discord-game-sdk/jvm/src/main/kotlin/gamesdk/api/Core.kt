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

package gamesdk.api

import gamesdk.api.managers.*
import gamesdk.api.types.DiscordClientId
import gamesdk.api.types.DiscordCreateFlags
import gamesdk.api.types.DiscordLogLevel
import gamesdk.impl.NativeCoreImpl

public interface Core : NativeObject.Closable {
    public val applicationManager: ApplicationManager
    public val userManager: UserManager
    public val imageManager: ImageManager
    public val activityManager: ActivityManager
    public val relationshipManager: RelationshipManager
    public val lobbyManager: LobbyManager
    public val networkManager: NetworkManager
    public val overlayManager: OverlayManager
    public val storageManager: StorageManager
    public val storeManager: StoreManager
    public val voiceManager: VoiceManager
    public val achievementManager: AchievementManager

    public fun runCallbacks(): DiscordResult
    public fun setLogHook(minLevel: DiscordLogLevel, hook: (level: DiscordLogLevel, message: String) -> Unit)

    public companion object {
        public fun create(clientId: DiscordClientId, createFlags: DiscordCreateFlags): DiscordCoreResult =
            NativeCoreImpl.create(clientId, createFlags)
    }
}
