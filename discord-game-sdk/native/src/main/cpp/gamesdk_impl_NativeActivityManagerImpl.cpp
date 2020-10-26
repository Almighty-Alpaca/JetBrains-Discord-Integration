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
#include "core.h"
#include "utils.h"

JNIEXPORT jint JNICALL Java_gamesdk_impl_NativeActivityManagerImplKt_registerCommand(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManagerPointer, jstring jCommand)
{
    discord::ActivityManager *activityManager = (discord::ActivityManager *)jActivityManagerPointer;

    const char *command = env->GetStringUTFChars(jCommand, nullptr);

    discord::Result result = activityManager->RegisterCommand(command);

    env->ReleaseStringUTFChars(jCommand, command);

    return (jint)result;
}

JNIEXPORT jint JNICALL Java_gamesdk_impl_NativeActivityManagerImplKt_registerSteam_0002dw0wvITU(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManagerPointer, jint jSteamId)
{
    discord::ActivityManager *activityManager = (discord::ActivityManager *)jActivityManagerPointer;

    discord::Result result = activityManager->RegisterSteam((uint32_t)jSteamId);

    return (jint)result;
}

discord::Activity construct_activity(JNIEnv *env, jobject jDestructuredActivity)
{
    jclass jDestructuredActivityClass = env->GetObjectClass(jDestructuredActivity);

    /// type signatures: https://docs.oracle.com/en/java/javase/15/docs/specs/jni/types.html#type-signatures
    jfieldID type_field_id = env->GetFieldID(jDestructuredActivityClass, "type", "I"),
             application_id_field_id = env->GetFieldID(jDestructuredActivityClass, "applicationId", "J"),
             name_field_id = env->GetFieldID(jDestructuredActivityClass, "name", "Ljava/lang/String;"),
             state_field_id = env->GetFieldID(jDestructuredActivityClass, "state", "Ljava/lang/String;"),
             details_field_id = env->GetFieldID(jDestructuredActivityClass, "details", "Ljava/lang/String;"),
             timestamp_start_field_id = env->GetFieldID(jDestructuredActivityClass, "timestampStart", "J"),
             timestamp_end_field_id = env->GetFieldID(jDestructuredActivityClass, "timestampEnd", "J"),
             assets_large_image_field_id = env->GetFieldID(jDestructuredActivityClass, "assetsLargeImage", "Ljava/lang/String;"),
             assets_large_text_field_id = env->GetFieldID(jDestructuredActivityClass, "assetsLargeText", "Ljava/lang/String;"),
             assets_small_image_field_id = env->GetFieldID(jDestructuredActivityClass, "assetsSmallImage", "Ljava/lang/String;"),
             assets_small_text_field_id = env->GetFieldID(jDestructuredActivityClass, "assetsSmallText", "Ljava/lang/String;"),
             party_id_field_id = env->GetFieldID(jDestructuredActivityClass, "partyId", "Ljava/lang/String;"),
             party_current_size_field_id = env->GetFieldID(jDestructuredActivityClass, "partyCurrentSize", "I"),
             party_max_size_field_id = env->GetFieldID(jDestructuredActivityClass, "partyMaxSize", "I"),
             party_privacy_field_id = env->GetFieldID(jDestructuredActivityClass, "partyPrivacy", "I"),
             secrets_match_field_id = env->GetFieldID(jDestructuredActivityClass, "secretsMatch", "Ljava/lang/String;"),
             secrets_join_field_id = env->GetFieldID(jDestructuredActivityClass, "secretsJoin", "Ljava/lang/String;"),
             secrets_spectate_field_id = env->GetFieldID(jDestructuredActivityClass, "secretsSpectate", "Ljava/lang/String;"),
             instance_field_id = env->GetFieldID(jDestructuredActivityClass, "instance", "Z");

    jint type = env->GetIntField(jDestructuredActivity, type_field_id);
    jlong application_id = env->GetLongField(jDestructuredActivity, application_id_field_id);
    jstring name = (jstring)env->GetObjectField(jDestructuredActivity, name_field_id);
    jstring state = (jstring)env->GetObjectField(jDestructuredActivity, state_field_id);
    jstring details = (jstring)env->GetObjectField(jDestructuredActivity, details_field_id);
    jlong timestamp_start = env->GetLongField(jDestructuredActivity, timestamp_start_field_id);
    jlong timestamp_end = env->GetLongField(jDestructuredActivity, timestamp_end_field_id);
    jstring assets_large_image = (jstring)env->GetObjectField(jDestructuredActivity, assets_large_image_field_id);
    jstring assets_large_text = (jstring)env->GetObjectField(jDestructuredActivity, assets_large_text_field_id);
    jstring assets_small_image = (jstring)env->GetObjectField(jDestructuredActivity, assets_small_image_field_id);
    jstring assets_small_text = (jstring)env->GetObjectField(jDestructuredActivity, assets_small_text_field_id);
    jstring party_id = (jstring)env->GetObjectField(jDestructuredActivity, party_id_field_id);
    jint party_current_size = env->GetIntField(jDestructuredActivity, party_current_size_field_id);
    jint party_max_size = env->GetIntField(jDestructuredActivity, party_max_size_field_id);
    jint party_privacy = env->GetIntField(jDestructuredActivity, party_privacy_field_id);
    jstring secrets_match = (jstring)env->GetObjectField(jDestructuredActivity, secrets_match_field_id);
    jstring secrets_join = (jstring)env->GetObjectField(jDestructuredActivity, secrets_join_field_id);
    jstring secrets_spectate = (jstring)env->GetObjectField(jDestructuredActivity, secrets_spectate_field_id);
    bool instance = env->GetBooleanField(jDestructuredActivity, instance_field_id);

    const char *name_native = env->GetStringUTFChars(name, nullptr);
    const char *state_native = env->GetStringUTFChars(state, nullptr);
    const char *details_native = env->GetStringUTFChars(details, nullptr);
    const char *assets_large_image_native = env->GetStringUTFChars(assets_large_image, nullptr);
    const char *assets_large_text_native = env->GetStringUTFChars(assets_large_text, nullptr);
    const char *assets_small_image_native = env->GetStringUTFChars(assets_small_image, nullptr);
    const char *assets_small_text_native = env->GetStringUTFChars(assets_small_text, nullptr);
    const char *party_id_native = env->GetStringUTFChars(party_id, nullptr);
    const char *secrets_match_native = env->GetStringUTFChars(secrets_match, nullptr);
    const char *secrets_join_native = env->GetStringUTFChars(secrets_match, nullptr);
    const char *secrets_spectate_native = env->GetStringUTFChars(secrets_match, nullptr);

    discord::Activity activity;
    activity.SetType((discord::ActivityType)type);
    activity.SetApplicationId(application_id);

    activity.SetName(name_native);
    activity.SetState(state_native);
    activity.SetDetails(details_native);

    activity.GetTimestamps().SetStart(timestamp_start);
    activity.GetTimestamps().SetEnd(timestamp_end);

    activity.GetAssets().SetLargeImage(assets_large_image_native);
    activity.GetAssets().SetLargeText(assets_large_text_native);
    activity.GetAssets().SetSmallImage(assets_small_image_native);
    activity.GetAssets().SetSmallText(assets_small_text_native);

    activity.GetParty().SetId(party_id_native);
    activity.GetParty().GetSize().SetCurrentSize(party_current_size);
    activity.GetParty().GetSize().SetMaxSize(party_max_size);
    activity.GetParty().SetPrivacy((discord::ActivityPartyPrivacy)party_privacy);

    activity.GetSecrets().SetMatch(secrets_match_native);
    activity.GetSecrets().SetJoin(secrets_join_native);
    activity.GetSecrets().SetSpectate(secrets_spectate_native);

    activity.SetInstance(instance);

    env->ReleaseStringUTFChars(name, name_native);
    env->ReleaseStringUTFChars(state, state_native);
    env->ReleaseStringUTFChars(details, details_native);

    env->ReleaseStringUTFChars(assets_large_image, assets_large_image_native);
    env->ReleaseStringUTFChars(assets_large_text, assets_large_text_native);
    env->ReleaseStringUTFChars(assets_small_image, assets_small_image_native);
    env->ReleaseStringUTFChars(assets_small_text, assets_small_text_native);

    env->ReleaseStringUTFChars(party_id, party_id_native);

    env->ReleaseStringUTFChars(secrets_match, secrets_match_native);
    env->ReleaseStringUTFChars(secrets_join, secrets_join_native);
    env->ReleaseStringUTFChars(secrets_spectate, secrets_spectate_native);

    return activity;
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeActivityManagerImplKt_updateActivity(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManagerPointer, jobject jDeconstructedActivity, jobject jCallback)
{
    discord::ActivityManager *activityManager = (discord::ActivityManager *)jActivityManagerPointer;
    discord::Activity activity = construct_activity(env, jDeconstructedActivity);

    std::function<void(discord::Result)> callback = createResultCallback(env, jCallback);

    activityManager->UpdateActivity(activity, callback);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeActivityManagerImplKt_clearActivity(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManagerPointer, jobject jCallback)
{
    discord::ActivityManager *activityManager = (discord::ActivityManager *)jActivityManagerPointer;

    std::function<void(discord::Result)> callback = createResultCallback(env, jCallback);

    activityManager->ClearActivity(callback);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeActivityManagerImplKt_sendRequestReply(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManagerPointer, jlong jUserId, jint jReply, jobject jCallback)
{
    discord::ActivityManager *activityManager = (discord::ActivityManager *)jActivityManagerPointer;

    std::function<void(discord::Result)> callback = createResultCallback(env, jCallback);

    activityManager->SendRequestReply((discord::UserId)jUserId, (discord::ActivityJoinRequestReply)jReply, callback);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeActivityManagerImplKt_sendInvite(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManagerPointer, jlong jUserId, jint jType, jstring jContent, jobject jCallback)
{
    discord::ActivityManager *activityManager = (discord::ActivityManager *)jActivityManagerPointer;

    std::function<void(discord::Result)> callback = createResultCallback(env, jCallback);

    const char *content = env->GetStringUTFChars(jContent, nullptr);

    activityManager->SendInvite((discord::UserId)jUserId, (discord::ActivityActionType)jType, content, callback);

    env->ReleaseStringUTFChars(jContent, content);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeActivityManagerImplKt_acceptInvite(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jActivityManagerPointer, jlong jUserId, jobject jCallback)
{
    discord::ActivityManager *activityManager = (discord::ActivityManager *)jActivityManagerPointer;

    std::function<void(discord::Result)> callback = createResultCallback(env, jCallback);

    activityManager->AcceptInvite((discord::UserId)jUserId, callback);
}
