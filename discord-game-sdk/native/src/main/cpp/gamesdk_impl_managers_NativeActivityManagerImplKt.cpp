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
#include "instance.h"
#include "types.h"

#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCUnusedGlobalDeclarationInspection"
#pragma ide diagnostic ignored "UnusedParameter"

JNIEXPORT jint JNICALL Java_gamesdk_impl_managers_NativeActivityManagerImplKt_registerCommand(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jPointer, jbyteArray jCommand
) {
    IDiscordActivityManager *activityManager = instance::getActivityManager((Instance *) jPointer);

    const auto command = types::createNativeString(*env, jCommand);

    EDiscordResult result = activityManager->register_command(activityManager, command.c_str());

    return (jint) result;
}

JNIEXPORT jint JNICALL Java_gamesdk_impl_managers_NativeActivityManagerImplKt_registerSteam_0002d99SHSdE(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jPointer, jint jSteamId
) {
    IDiscordActivityManager *activityManager = instance::getActivityManager((Instance *) jPointer);

    EDiscordResult result = activityManager->register_steam(activityManager, (uint32_t) jSteamId);

    return (jint) result;
}

JNIEXPORT void JNICALL Java_gamesdk_impl_managers_NativeActivityManagerImplKt_updateActivity(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jPointer, jobject jDeconstructedActivity, jobject jCallback
) {
    IDiscordActivityManager *activityManager = instance::getActivityManager((Instance *) jPointer);

    DiscordActivity activity = types::createDiscordActivity(*env, jDeconstructedActivity);

    activityManager->update_activity(activityManager, &activity, callback::create(env, jCallback), callback::run);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_managers_NativeActivityManagerImplKt_clearActivity(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jPointer, jobject jCallback
) {
    IDiscordActivityManager *activityManager = instance::getActivityManager((Instance *) jPointer);

    activityManager->clear_activity(activityManager, callback::create(env, jCallback), callback::run);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_managers_NativeActivityManagerImplKt_sendRequestReply(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jPointer, jlong jUserId, jint jReply, jobject jCallback
) {
    IDiscordActivityManager *activityManager = instance::getActivityManager((Instance *) jPointer);

    activityManager->send_request_reply(activityManager, (DiscordUserId) jUserId, (EDiscordActivityJoinRequestReply) jReply, callback::create(env, jCallback), callback::run);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_managers_NativeActivityManagerImplKt_sendInvite(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jPointer, jlong jUserId, jint jType, jbyteArray jContent, jobject jCallback
) {
    IDiscordActivityManager *activityManager = instance::getActivityManager((Instance *) jPointer);

    const auto content = types::createNativeString(*env, jContent);

    activityManager->send_invite(activityManager, (DiscordUserId) jUserId, (EDiscordActivityActionType) jType, content.c_str(), callback::create(env, jCallback), callback::run);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_managers_NativeActivityManagerImplKt_acceptInvite(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jPointer, jlong jUserId, jobject jCallback
) {
    IDiscordActivityManager *activityManager = instance::getActivityManager((Instance *) jPointer);

    activityManager->accept_invite(activityManager, (DiscordUserId) jUserId, callback::create(env, jCallback), callback::run);
}

#pragma clang diagnostic pop
