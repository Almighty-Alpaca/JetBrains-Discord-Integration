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

package com.almightyalpaca.jetbrains.plugins.discord.gamesdk

interface DiscordLobbyTransaction {
    fun setType(type: DiscordLobbyType): DiscordResult
    fun setOwner(ownerId: DiscordUserId): DiscordResult
    fun setCapacity(capacity: uint32_t): DiscordResult
    fun setMetadata(
        metadataKey: DiscordMetadataKey,
        metadataValue: DiscordMetadataValue
    ): DiscordResult
    fun deleteMetadata(metadataKey: DiscordMetadataKey): DiscordResult
    fun setLocked(locked: Boolean): DiscordResult
}

interface DiscordLobbyMemberTransaction {
    fun setMetadata(
        metadataKey: DiscordMetadataKey,
        metadataValue: DiscordMetadataValue
    ): DiscordResult
    fun deleteMetadata(metadataKey: DiscordMetadataKey): DiscordResult
}

interface DiscordLobbySearchQuery {
    fun filter(
        key: DiscordMetadataKey,
        comparison: DiscordLobbySearchComparison,
        cast: DiscordLobbySearchCast,
        value: DiscordMetadataValue
    ): DiscordResult
    fun sort(
        key: DiscordMetadataKey,
        cast: DiscordLobbySearchCast,
        value: DiscordMetadataValue
    ): DiscordResult
    fun limit(limit: uint32_t): DiscordResult
    fun distance(distance: DiscordLobbySearchDistance): DiscordResult
}

// TODO: try to replace pointers with object refs

interface ApplicationManager {
    fun <T> validateOrExit(
        callbackData: @Pointer<T> Long,
        callback: (callbackData: @Pointer<T> Long, result: DiscordResult) -> Unit
    )
    fun getCurrentLocale(locale: @Pointer<DiscordLocale> Long)
    fun getCurrentBranch(branch: @Pointer<DiscordBranch> Long)
    fun getOAuth2Token(token: @Pointer<DiscordOAuth2Token> Long)
    fun <T> getTicket(
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult,
            ticket: String
        ) -> Unit
    )
}

interface DiscordUserEvents {
    fun onCurrentUserUpdate(data: @VoidPointer Long)
}

interface DiscordUserManager {
    fun getCurrentUser(user: @Pointer<DiscordUser> Long): DiscordResult
    fun <T> getUser(
        userId: DiscordUserId,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult,
            user: @Pointer<DiscordUser> Long
        ) -> Unit
    )
    fun getCurrentUserPremiumType(premiumType: @Pointer<DiscordPremiumType> Long): DiscordResult
    fun currentUserHasFlag(
        flag: DiscordUserFlag,
        hasFlag: Boolean
    ): DiscordResult
}

interface DiscordImageManager {
    fun <T> fetch(
        handle: DiscordImageHandle,
        refresh: Boolean,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult,
            result_handle: DiscordImageHandle
        ) -> Unit
    )

    fun getDimensions(
        handle: DiscordImageHandle,
        dimensions: @Pointer<DiscordImageDimensions> Long
    )
    fun getData(
        handle: DiscordImageHandle,
        data: @Pointer<uint8_t> Long,
        data_length: uint32_t
    )
}

interface ActivityEvents {
    fun onActivityJoin(
        eventData: @VoidPointer Long,
        secret: String
    )
    fun onActivitySpectate(
        eventData: @VoidPointer Long,
        secret: String
    )
    fun onActivityJoinRequest(
        eventData: @VoidPointer Long,
        user: @Pointer<DiscordUser> Long
    )
    fun onActivityInvite(
        event_data: @VoidPointer Long,
        type: DiscordActivityActionType,
        user: @Pointer<DiscordUser> Long,
        activity: @Pointer<DiscordActivity> Long
    )
}

interface DiscordActivityManager {
    fun registerCommand(command: String): DiscordResult
    fun registerSteam(steamId: uint32_t): DiscordResult
    fun <T> updateActivity(
        activity: @Pointer<DiscordActivity> Long,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )
    fun <T> clearActivity(
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )
    fun <T> sendRequestReply(
        userId: DiscordUserId,
        reply: DiscordActivityJoinRequestReply,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )
    fun <T> sendInvite(
        userId: DiscordUserId,
        type: DiscordActivityActionType,
        content: String,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )

    fun <T> acceptInvite(
        userId: DiscordUserId,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )
}

interface DiscordRelationshipEvents {
    fun onRefresh(eventData: @VoidPointer Long)
    fun onRelationshipUpdate(
        eventData: @VoidPointer Long,
        relationship: @Pointer<DiscordRelationship> Long
    )
}

interface DiscordRelationshipManager {
    fun <T> filter(
        filterData: @Pointer<T> Long,
        filter: (
            filterData: @Pointer<T> Long,
            relationship: @Pointer<DiscordRelationship> Long
        ) -> Boolean
    )
    fun count(count: @Pointer<int32_t> Long): DiscordResult
    fun get(
        userId: DiscordUserId,
        relationship: @Pointer<DiscordRelationship> Long
    ): DiscordResult
    fun getAt(
        index: uint32_t,
        relationship: @Pointer<DiscordRelationship> Long
    ): DiscordResult
}

interface DiscordLobbyEvents {
    fun onLobbyUpdate(
        eventData: @VoidPointer Long,
        lobbyId: int64_t
    )
    fun onLobbyDelete(
        eventData: @VoidPointer Long,
        lobbyId: int64_t,
        reason: uint32_t
    )
    fun onMemberConnect(
        event_data: @VoidPointer Long,
        lobbyId: int64_t,
        userId: int64_t
    )
    fun onMemberUpdate(
        eventData: @VoidPointer Long,
        lobbyId: int64_t,
        userId: int64_t
    )
    fun onMemberDisconnect(
        eventData: @VoidPointer Long,
        lobbyId: int64_t,
        userId: int64_t
    )
    fun onLobbyMessage(
        eventData: @VoidPointer Long,
        lobbyId: int64_t,
        userId: int64_t,
        data: @Pointer<uint8_t> Long,
        dataLength: uint32_t
    )
    fun onSpeaking(
        eventData: @VoidPointer Long,
        lobbyId: int64_t,
        userId: int64_t,
        speaking: Boolean
    )
    fun onNetworkMessage(
        eventData: @VoidPointer Long,
        lobbyId: int64_t,
        userId: int64_t,
        channelId: uint8_t,
        data: @Pointer<uint8_t> Long,
        dataLength: uint32_t
    )
}

interface DiscordLobbyManager {
    fun getLobbyCreateTransaction(transaction: @DoublePointer<DiscordLobbyTransaction> Long): DiscordResult
    fun getLobbyUpdateTransaction(
        lobbyId: DiscordLobbyId,
        transaction: @DoublePointer<DiscordLobbyTransaction> Long
    ): DiscordResult
    fun getMemberUpdateTransaction(
        lobbyId: DiscordLobbyId,
        userId: DiscordUserId,
        transaction: @DoublePointer<DiscordLobbyTransaction> Long
    ): DiscordResult
    fun <T> createLobby(
        transaction: @Pointer<DiscordLobbyTransaction> Long,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult,
            lobby: @Pointer<DiscordLobby> Long
        ) -> Unit
    )
    fun <T> updateLobby(
        lobbyId: DiscordLobbyId,
        transaction: @Pointer<DiscordLobbyTransaction> Long,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    );
    fun <T> deleteLobby(
        lobbyId: DiscordLobbyId,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    );
    fun <T> connectLobby(
        lobbyId: DiscordLobbyId,
        secret: DiscordLobbySecret,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult,
            lobby: @Pointer<DiscordLobby> Long
        ) -> Unit
    );
    fun <T> connect_lobby_with_activity_secret(
        activity_secret: DiscordLobbySecret,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult,
            lobby: @Pointer<DiscordLobby> Long
        ) -> Unit
    );
    void (*disconnect_lobby)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id, void* callback_data, void (*callback)(void* callback_data,
    enum EDiscordResult result));
    enum EDiscordResult(*get_lobby)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id, struct DiscordLobby* lobby);
    enum EDiscordResult(*get_lobby_activity_secret)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id, DiscordLobbySecret* secret);
    enum EDiscordResult(*get_lobby_metadata_value)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id, DiscordMetadataKey key, DiscordMetadataValue* value);
    enum EDiscordResult(*get_lobby_metadata_key)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id, int32_t index, DiscordMetadataKey* key);
    enum EDiscordResult(*lobby_metadata_count)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id, int32_t* count);
    enum EDiscordResult(*member_count)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id, int32_t* count);
    enum EDiscordResult(*get_member_user_id)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id, int32_t index, DiscordUserId* user_id);
    enum EDiscordResult(*get_member_user)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id, DiscordUserId user_id, struct DiscordUser* user);
    enum EDiscordResult(*get_member_metadata_value)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id, DiscordUserId user_id, DiscordMetadataKey key, DiscordMetadataValue* value);
    enum EDiscordResult(*get_member_metadata_key)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id, DiscordUserId user_id, int32_t index, DiscordMetadataKey* key);
    enum EDiscordResult(*member_metadata_count)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id, DiscordUserId user_id, int32_t* count);
    void (*update_member)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id, DiscordUserId user_id, struct IDiscordLobbyMemberTransaction* transaction, void* callback_data, void (*callback)(void* callback_data,
    enum EDiscordResult result));
    void (*send_lobby_message)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id, uint8_t* data , uint32_t data_length, void* callback_data, void (*callback)(void* callback_data,
    enum EDiscordResult result));
    enum EDiscordResult(*get_search_query)(struct IDiscordLobbyManager* manager, struct IDiscordLobbySearchQuery** query);
    void (*search)(struct IDiscordLobbyManager* manager, struct IDiscordLobbySearchQuery* query, void* callback_data, void (*callback)(void* callback_data,
    enum EDiscordResult result));
    void (*lobby_count)(struct IDiscordLobbyManager* manager, int32_t* count);
    enum EDiscordResult(*get_lobby_id)(struct IDiscordLobbyManager* manager, int32_t index, DiscordLobbyId* lobby_id);
    void (*connect_voice)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id, void* callback_data, void (*callback)(void* callback_data,
    enum EDiscordResult result));
    void (*disconnect_voice)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id, void* callback_data, void (*callback)(void* callback_data,
    enum EDiscordResult result));
    enum EDiscordResult(*connect_network)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id);
    enum EDiscordResult(*disconnect_network)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id);
    enum EDiscordResult(*flush_network)(struct IDiscordLobbyManager* manager);
    enum EDiscordResult(*open_network_channel)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id, uint8_t channel_id, bool reliable);
    enum EDiscordResult(*send_network_message)(struct IDiscordLobbyManager* manager, DiscordLobbyId lobby_id, DiscordUserId user_id, uint8_t channel_id, uint8_t* data , uint32_t data_length);
};

struct IDiscordNetworkEvents {
    void(*on_message)(void * event_data, DiscordNetworkPeerId peer_id, DiscordNetworkChannelId channel_id, uint8_t * data, uint32_t data_length);
    void(*on_route_update)(void * event_data, const char * route_data);
};

struct IDiscordNetworkManager {
    /**
     * Get the local peer ID for this process.
     */
    void(*get_peer_id)(struct IDiscordNetworkManager * manager, DiscordNetworkPeerId * peer_id);
    /**
     * Send pending network messages.
     */
    enum EDiscordResult ( * flush)(struct IDiscordNetworkManager* manager);
    /**
     * Open a connection to a remote peer.
     */
    enum EDiscordResult ( * open_peer)(struct IDiscordNetworkManager* manager, DiscordNetworkPeerId peer_id, const char* route_data);
    /**
     * Update the route data for a connected peer.
     */
    enum EDiscordResult ( * update_peer)(struct IDiscordNetworkManager* manager, DiscordNetworkPeerId peer_id, const char* route_data);
    /**
     * Close the connection to a remote peer.
     */
    enum EDiscordResult ( * close_peer)(struct IDiscordNetworkManager* manager, DiscordNetworkPeerId peer_id);
    /**
     * Open a message channel to a connected peer.
     */
    enum EDiscordResult ( * open_channel)(struct IDiscordNetworkManager* manager, DiscordNetworkPeerId peer_id, DiscordNetworkChannelId channel_id, bool reliable);
    /**
     * Close a message channel to a connected peer.
     */
    enum EDiscordResult ( * close_channel)(struct IDiscordNetworkManager* manager, DiscordNetworkPeerId peer_id, DiscordNetworkChannelId channel_id);
    /**
     * Send a message to a connected peer over an opened message channel.
     */
    enum EDiscordResult ( * send_message)(struct IDiscordNetworkManager* manager, DiscordNetworkPeerId peer_id, DiscordNetworkChannelId channel_id, uint8_t* data, uint32_t data_length);
};

struct IDiscordOverlayEvents {
    void(*on_toggle)(void * event_data, bool locked);
};

struct IDiscordOverlayManager {
    void(*is_enabled)(struct IDiscordOverlayManager * manager, bool * enabled);
    void(*is_locked)(struct IDiscordOverlayManager * manager, bool * locked);
    void(*set_locked)(struct IDiscordOverlayManager * manager, bool locked, void * callback_data, void(*callback)(void * callback_data, enum EDiscordResult result));
    void(*open_activity_invite)(struct IDiscordOverlayManager * manager, enum EDiscordActivityActionType type, void * callback_data, void(*callback)(void * callback_data, enum EDiscordResult result));
    void(*open_guild_invite)(struct IDiscordOverlayManager * manager, const char * code, void * callback_data, void(*callback)(void * callback_data, enum EDiscordResult result));
    void(*open_voice_settings)(struct IDiscordOverlayManager * manager, void * callback_data, void(*callback)(void * callback_data, enum EDiscordResult result));
};

typedef void* IDiscordStorageEvents;

struct IDiscordStorageManager {
    enum EDiscordResult ( * read)(struct IDiscordStorageManager* manager, const char* name, uint8_t* data, uint32_t data_length, uint32_t* read);
    void(*read_async)(
        struct IDiscordStorageManager * manager,
        const char * name,
        void * callback_data,
        void(*callback)(void * callback_data, enum EDiscordResult result, uint8_t * data, uint32_t data_length)
    );
    void(*read_async_partial)(
        struct IDiscordStorageManager * manager,
        const char * name,
        uint64_t offset,
        uint64_t length,
        void * callback_data,
        void(*callback)(void * callback_data, enum EDiscordResult result, uint8_t * data, uint32_t data_length)
    );
    enum EDiscordResult ( * write)(struct IDiscordStorageManager* manager, const char* name, uint8_t* data, uint32_t data_length);
    void(*write_async)(
        struct IDiscordStorageManager * manager,
        const char * name,
        uint8_t * data,
        uint32_t data_length,
        void * callback_data,
        void(*callback)(void * callback_data, enum EDiscordResult result)
    );
    enum EDiscordResult ( * delete_)(struct IDiscordStorageManager* manager, const char* name);
    enum EDiscordResult ( * exists)(struct IDiscordStorageManager* manager, const char* name, bool* exists);
    void(*count)(struct IDiscordStorageManager * manager, int32_t * count);
    enum EDiscordResult ( * stat)(struct IDiscordStorageManager* manager, const char* name, struct DiscordFileStat* stat);
    enum EDiscordResult ( * stat_at)(struct IDiscordStorageManager* manager, int32_t index, struct DiscordFileStat* stat);
    enum EDiscordResult ( * get_path)(struct IDiscordStorageManager* manager, DiscordPath* path);
};

struct IDiscordStoreEvents {
    void(*on_entitlement_create)(void * event_data, struct DiscordEntitlement * entitlement);
    void(*on_entitlement_delete)(void * event_data, struct DiscordEntitlement * entitlement);
};

struct IDiscordStoreManager {
    void(*fetch_skus)(struct IDiscordStoreManager * manager, void * callback_data, void(*callback)(void * callback_data, enum EDiscordResult result));
    void(*count_skus)(struct IDiscordStoreManager * manager, int32_t * count);
    enum EDiscordResult ( * get_sku)(struct IDiscordStoreManager* manager, DiscordSnowflake sku_id, struct DiscordSku* sku);
    enum EDiscordResult ( * get_sku_at)(struct IDiscordStoreManager* manager, int32_t index, struct DiscordSku* sku);
    void(*fetch_entitlements)(struct IDiscordStoreManager * manager, void * callback_data, void(*callback)(void * callback_data, enum EDiscordResult result));
    void(*count_entitlements)(struct IDiscordStoreManager * manager, int32_t * count);
    enum EDiscordResult ( * get_entitlement)(struct IDiscordStoreManager* manager, DiscordSnowflake entitlement_id, struct DiscordEntitlement* entitlement);
    enum EDiscordResult ( * get_entitlement_at)(struct IDiscordStoreManager* manager, int32_t index, struct DiscordEntitlement* entitlement);
    enum EDiscordResult ( * has_sku_entitlement)(struct IDiscordStoreManager* manager, DiscordSnowflake sku_id, bool* has_entitlement);
    void(*start_purchase)(struct IDiscordStoreManager * manager, DiscordSnowflake sku_id, void * callback_data, void(*callback)(void * callback_data, enum EDiscordResult result));
};

struct IDiscordVoiceEvents {
    void(*on_settings_update)(void * event_data);
};

struct IDiscordVoiceManager {
    enum EDiscordResult ( * get_input_mode)(struct IDiscordVoiceManager* manager, struct DiscordInputMode* input_mode);
    void(*set_input_mode)(struct IDiscordVoiceManager * manager, struct DiscordInputMode input_mode, void * callback_data, void(*callback)(void * callback_data, enum EDiscordResult result));
    enum EDiscordResult ( * is_self_mute)(struct IDiscordVoiceManager* manager, bool* mute);
    enum EDiscordResult ( * set_self_mute)(struct IDiscordVoiceManager* manager, bool mute);
    enum EDiscordResult ( * is_self_deaf)(struct IDiscordVoiceManager* manager, bool* deaf);
    enum EDiscordResult ( * set_self_deaf)(struct IDiscordVoiceManager* manager, bool deaf);
    enum EDiscordResult ( * is_local_mute)(struct IDiscordVoiceManager* manager, DiscordSnowflake user_id, bool* mute);
    enum EDiscordResult ( * set_local_mute)(struct IDiscordVoiceManager* manager, DiscordSnowflake user_id, bool mute);
    enum EDiscordResult ( * get_local_volume)(struct IDiscordVoiceManager* manager, DiscordSnowflake user_id, uint8_t* volume);
    enum EDiscordResult ( * set_local_volume)(struct IDiscordVoiceManager* manager, DiscordSnowflake user_id, uint8_t volume);
};

struct IDiscordAchievementEvents {
    void(*on_user_achievement_update)(void * event_data, struct DiscordUserAchievement * user_achievement);
};

struct IDiscordAchievementManager {
    void(*set_user_achievement)(
        struct IDiscordAchievementManager * manager,
        DiscordSnowflake achievement_id,
        uint8_t percent_complete,
        void * callback_data,
        void(*callback)(void * callback_data, enum EDiscordResult result)
    );
    void(*fetch_user_achievements)(struct IDiscordAchievementManager * manager, void * callback_data, void(*callback)(void * callback_data, enum EDiscordResult result));
    void(*count_user_achievements)(struct IDiscordAchievementManager * manager, int32_t * count);
    enum EDiscordResult ( * get_user_achievement)(struct IDiscordAchievementManager* manager, DiscordSnowflake user_achievement_id, struct DiscordUserAchievement* user_achievement);
    enum EDiscordResult ( * get_user_achievement_at)(struct IDiscordAchievementManager* manager, int32_t index, struct DiscordUserAchievement* user_achievement);
};

typedef void* IDiscordCoreEvents;

struct IDiscordCore {
    void(*destroy)(struct IDiscordCore * core);
    enum EDiscordResult ( * run_callbacks)(struct IDiscordCore* core);
    void(*set_log_hook)(struct IDiscordCore * core, enum EDiscordLogLevel min_level, void * hook_data, void(*hook)(void * hook_data, enum EDiscordLogLevel level, const char * message));
    struct IDiscordApplicationManager *(*get_application_manager)(struct IDiscordCore * core);
    struct IDiscordUserManager *(*get_user_manager)(struct IDiscordCore * core);
    struct IDiscordImageManager *(*get_image_manager)(struct IDiscordCore * core);
    struct IDiscordActivityManager *(*get_activity_manager)(struct IDiscordCore * core);
    struct IDiscordRelationshipManager *(*get_relationship_manager)(struct IDiscordCore * core);
    struct IDiscordLobbyManager *(*get_lobby_manager)(struct IDiscordCore * core);
    struct IDiscordNetworkManager *(*get_network_manager)(struct IDiscordCore * core);
    struct IDiscordOverlayManager *(*get_overlay_manager)(struct IDiscordCore * core);
    struct IDiscordStorageManager *(*get_storage_manager)(struct IDiscordCore * core);
    struct IDiscordStoreManager *(*get_store_manager)(struct IDiscordCore * core);
    struct IDiscordVoiceManager *(*get_voice_manager)(struct IDiscordCore * core);
    struct IDiscordAchievementManager *(*get_achievement_manager)(struct IDiscordCore * core);
};

struct DiscordCreateParams {
    DiscordClientId client_id;
    uint64_t flags;
    IDiscordCoreEvents * events;
    void * event_data;
    IDiscordApplicationEvents * application_events;
    DiscordVersion application_version;
    struct IDiscordUserEvents * user_events;
    DiscordVersion user_version;
    IDiscordImageEvents * image_events;
    DiscordVersion image_version;
    struct IDiscordActivityEvents * activity_events;
    DiscordVersion activity_version;
    struct IDiscordRelationshipEvents * relationship_events;
    DiscordVersion relationship_version;
    struct IDiscordLobbyEvents * lobby_events;
    DiscordVersion lobby_version;
    struct IDiscordNetworkEvents * network_events;
    DiscordVersion network_version;
    struct IDiscordOverlayEvents * overlay_events;
    DiscordVersion overlay_version;
    IDiscordStorageEvents * storage_events;
    DiscordVersion storage_version;
    struct IDiscordStoreEvents * store_events;
    DiscordVersion store_version;
    struct IDiscordVoiceEvents * voice_events;
    DiscordVersion voice_version;
    struct IDiscordAchievementEvents * achievement_events;
    DiscordVersion achievement_version;
};

//fun DiscordCreateParamsSetDefault(struct DiscordCreateParams* params)
//{
//    memset(params, 0, sizeof(struct DiscordCreateParams));
//    params->application_version = DISCORD_APPLICATION_MANAGER_VERSION;
//    params->user_version = DISCORD_USER_MANAGER_VERSION;
//    params->image_version = DISCORD_IMAGE_MANAGER_VERSION;
//    params->activity_version = DISCORD_ACTIVITY_MANAGER_VERSION;
//    params->relationship_version = DISCORD_RELATIONSHIP_MANAGER_VERSION;
//    params->lobby_version = DISCORD_LOBBY_MANAGER_VERSION;
//    params->network_version = DISCORD_NETWORK_MANAGER_VERSION;
//    params->overlay_version = DISCORD_OVERLAY_MANAGER_VERSION;
//    params->storage_version = DISCORD_STORAGE_MANAGER_VERSION;
//    params->store_version = DISCORD_STORE_MANAGER_VERSION;
//    params->voice_version = DISCORD_VOICE_MANAGER_VERSION;
//    params->achievement_version = DISCORD_ACHIEVEMENT_MANAGER_VERSION;
//}

//enum EDiscordResult DiscordCreate(DiscordVersion version, struct DiscordCreateParams* params, struct IDiscordCore** result);

