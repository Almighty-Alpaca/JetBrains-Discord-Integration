/**
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

#include "gamesdk_impl_NativeCoreImplKt.h"

#include "commons.h"
#include "discord_game_sdk.h"
#include "events.h"

#include <iostream>

JNIEXPORT jobject JNICALL Java_gamesdk_impl_NativeCoreImplKt_create_0002d47qCbFM(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jClientId, jint jCreateFlags, jobject jEvents
) {
    DiscordCreateParams params{};
    DiscordCreateParamsSetDefault(&params);

    params.client_id = (DiscordClientId) jClientId;
    params.flags = (uint64_t) jCreateFlags;

    params.event_data = events::createData(env, jEvents);

    params.events = nullptr;

    // params.application_events = nullptr;
    params.user_events = events::getUserEvents();

    params.image_events = nullptr;
    // params.activity_events = new IDiscordActivityEvents{};

    params.relationship_events = events::getRelationshipEvents();

    // params.lobby_events = new IDiscordLobbyEvents{};
    // params.network_events = new IDiscordNetworkEvents{};
    // params.overlay_events = new IDiscordOverlayEvents{};
    params.storage_events = nullptr;
    // params.store_events = new IDiscordStoreEvents{};
    // params.voice_events = new IDiscordVoiceEvents{};
    // params.achievement_events = new IDiscordAchievementEvents{};

    IDiscordCore *core = nullptr;
    EDiscordResult result = DiscordCreate(DISCORD_VERSION, &params, &core);

    return createNativeDiscordObjectResult(env, result, createLongObject(env, (jlong) core));
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeCoreImplKt_destroy(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jCore) {
    auto *core = (IDiscordCore *) jCore;

    core->destroy(core);
}

JNIEXPORT jint JNICALL Java_gamesdk_impl_NativeCoreImplKt_runCallbacks(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jCore) {
    auto *core = (IDiscordCore *) jCore;

    return (jint) core->run_callbacks(core);
}

JNIEXPORT jlong JNICALL Java_gamesdk_impl_NativeCoreImplKt_getActivityManager(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jCore) {
    auto *core = (IDiscordCore *) jCore;

    return (jlong) core->get_activity_manager(core);
}

JNIEXPORT jlong JNICALL Java_gamesdk_impl_NativeCoreImplKt_getRelationshipManager(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jCore) {
    auto *core = (IDiscordCore *) jCore;

    return (jlong) core->get_relationship_manager(core);
}

JNIEXPORT jlong JNICALL Java_gamesdk_impl_NativeCoreImplKt_getUserManager(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jCore) {
    auto *core = (IDiscordCore *) jCore;

    return (jlong) core->get_user_manager(core);
}
