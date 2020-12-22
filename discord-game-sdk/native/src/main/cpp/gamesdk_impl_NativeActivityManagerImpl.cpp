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

#include "gamesdk_impl_NativeActivityManagerImplKt.h"

#include "discord_game_sdk.h"
#include "deconstructed.h"
#include "callback.h"

JNIEXPORT jint JNICALL Java_gamesdk_impl_NativeActivityManagerImplKt_registerCommand(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManagerPointer, jstring jCommand)
{
    struct IDiscordActivityManager *activityManager = (IDiscordActivityManager *)jActivityManagerPointer;

    const char *command = env->GetStringUTFChars(jCommand, nullptr);

    EDiscordResult result = activityManager->register_command(activityManager, command);

    env->ReleaseStringUTFChars(jCommand, command);

    return (jint)result;
}

JNIEXPORT jint JNICALL Java_gamesdk_impl_NativeActivityManagerImplKt_registerSteam_0002dw0wvITU(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManagerPointer, jint jSteamId)
{
    struct IDiscordActivityManager *activityManager = (IDiscordActivityManager *)jActivityManagerPointer;

    EDiscordResult result = activityManager->register_steam(activityManager, (uint32_t)jSteamId);

    return (jint)result;
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeActivityManagerImplKt_updateActivity(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManagerPointer, jobject jDeconstructedActivity, jobject jCallback)
{
    struct IDiscordActivityManager *activityManager = (IDiscordActivityManager *)jActivityManagerPointer;
    struct DiscordActivity activity = deconstructed::constructActivity(env, jDeconstructedActivity);

    activityManager->update_activity(activityManager, &activity, callback::getData(env, jCallback), callback::result::run);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeActivityManagerImplKt_clearActivity(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManagerPointer, jobject jCallback)
{
    struct IDiscordActivityManager *activityManager = (IDiscordActivityManager *)jActivityManagerPointer;

    activityManager->clear_activity(activityManager, callback::getData(env, jCallback), callback::result::run);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeActivityManagerImplKt_sendRequestReply(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManagerPointer, jlong jUserId, jint jReply, jobject jCallback)
{
    struct IDiscordActivityManager *activityManager = (IDiscordActivityManager *)jActivityManagerPointer;

    activityManager->send_request_reply(activityManager, (DiscordUserId)jUserId, (EDiscordActivityJoinRequestReply)jReply, callback::getData(env, jCallback), callback::result::run);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeActivityManagerImplKt_sendInvite(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManagerPointer, jlong jUserId, jint jType, jstring jContent, jobject jCallback)
{
    struct IDiscordActivityManager *activityManager = (IDiscordActivityManager *)jActivityManagerPointer;

    const char *content = env->GetStringUTFChars(jContent, nullptr);

    activityManager->send_invite(activityManager, (DiscordUserId)jUserId, (EDiscordActivityActionType)jType, content, callback::getData(env, jCallback), callback::result::run);

    env->ReleaseStringUTFChars(jContent, content);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeActivityManagerImplKt_acceptInvite(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManagerPointer, jlong jUserId, jobject jCallback)
{
    struct IDiscordActivityManager *activityManager = (IDiscordActivityManager *)jActivityManagerPointer;

    activityManager->accept_invite(activityManager, (DiscordUserId)jUserId, callback::getData(env, jCallback), callback::result::run);
}
