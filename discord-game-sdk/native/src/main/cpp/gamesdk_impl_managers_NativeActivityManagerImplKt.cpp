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

#include "gamesdk_impl_managers_NativeActivityManagerImplKt.h"

#include "callback.h"
#include "discord_game_sdk.h"
#include "types.h"

JNIEXPORT jint JNICALL Java_gamesdk_impl_managers_NativeActivityManagerImplKt_registerCommand(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManager, jstring jCommand
) {
    auto *activityManager = (IDiscordActivityManager *) jActivityManager;

    const char *command = env->GetStringUTFChars(jCommand, nullptr);

    EDiscordResult result = activityManager->register_command(activityManager, command);

    env->ReleaseStringUTFChars(jCommand, command);

    return (jint) result;
}

JNIEXPORT jint JNICALL Java_gamesdk_impl_managers_NativeActivityManagerImplKt_registerSteam_0002d99SHSdE(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManager, jint jSteamId
) {
    auto *activityManager = (IDiscordActivityManager *) jActivityManager;

    EDiscordResult result = activityManager->register_steam(activityManager, (uint32_t) jSteamId);

    return (jint) result;
}

JNIEXPORT void JNICALL Java_gamesdk_impl_managers_NativeActivityManagerImplKt_updateActivity(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManager, jobject jDeconstructedActivity, jobject jCallback
) {
    auto *activityManager = (IDiscordActivityManager *) jActivityManager;
    DiscordActivity activity = types::createDiscordActivity(*env, jDeconstructedActivity);

    activityManager->update_activity(activityManager, &activity, callback::create(env, jCallback), callback::run);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_managers_NativeActivityManagerImplKt_clearActivity(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManager, jobject jCallback
) {
    auto *activityManager = (IDiscordActivityManager *) jActivityManager;

    activityManager->clear_activity(activityManager, callback::create(env, jCallback), callback::run);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_managers_NativeActivityManagerImplKt_sendRequestReply(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManager, jlong jUserId, jint jReply, jobject jCallback
) {
    auto *activityManager = (IDiscordActivityManager *) jActivityManager;

    activityManager->send_request_reply(activityManager, (DiscordUserId) jUserId, (EDiscordActivityJoinRequestReply) jReply, callback::create(env, jCallback), callback::run);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_managers_NativeActivityManagerImplKt_sendInvite(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManager, jlong jUserId, jint jType, jstring jContent, jobject jCallback
) {
    auto *activityManager = (IDiscordActivityManager *) jActivityManager;

    const char *content = env->GetStringUTFChars(jContent, nullptr);

    activityManager->send_invite(activityManager, (DiscordUserId) jUserId, (EDiscordActivityActionType) jType, content, callback::create(env, jCallback), callback::run);

    env->ReleaseStringUTFChars(jContent, content);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_managers_NativeActivityManagerImplKt_acceptInvite(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManager, jlong jUserId, jobject jCallback
) {
    auto *activityManager = (IDiscordActivityManager *) jActivityManager;

    activityManager->accept_invite(activityManager, (DiscordUserId) jUserId, callback::create(env, jCallback), callback::run);
}
