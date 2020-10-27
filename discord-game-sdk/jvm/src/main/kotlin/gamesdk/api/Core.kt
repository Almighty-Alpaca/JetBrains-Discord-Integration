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

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api.*
import gamesdk.impl.NativeCoreImpl

typealias ClientId = Long

interface Core : NativeObject.Closable, DiscordCore {
    val applicationManager: ApplicationManager
    val userManager: UserManager
    val imageManager: ImageManager
    val activityManager: ActivityManager
    val relationshipManager: RelationshipManager
    val lobbyManager: LobbyManager
    val networkManager: NetworkManager
    val overlayManager: OverlayManager
    val storageManager: StorageManager
    val storeManager: StoreManager
    val voiceManager: VoiceManager
    val achievementManager: AchievementManager

    override fun runCallbacks(): DiscordResult
    override fun setLogHook(minLevel: DiscordLogLevel, hook: (level: DiscordLogLevel, message: String) -> Unit)

    override fun getApplicationManager(): DiscordApplicationManager = applicationManager
    override fun getUserManager(): DiscordUserManager = userManager
    override fun getImageManager(): DiscordImageManager = imageManager
    override fun getActivityManager(): DiscordActivityManager = activityManager
    override fun getRelationshipManager(): DiscordRelationshipManager = relationshipManager
    override fun getLobbyManager(): DiscordLobbyManager = lobbyManager
    override fun getNetworkManager(): DiscordNetworkManager = networkManager
    override fun getOverlayManager(): DiscordOverlayManager = overlayManager
    override fun getStorageManager(): DiscordStorageManager = storageManager
    override fun getStoreManager(): DiscordStoreManager = storeManager
    override fun getVoiceManager(): DiscordVoiceManager = voiceManager
    override fun getAchievementManager(): DiscordAchievementManager = achievementManager

    companion object {
        fun create(clientId: ClientId, createFlags: DiscordCreateFlags): Result<Core, DiscordResult> =
            NativeCoreImpl.create(clientId, createFlags)
    }
}
