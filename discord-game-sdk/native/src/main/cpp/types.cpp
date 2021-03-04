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

#include "types.h"

#include <iostream>

#include "jniclasses.h"

namespace types {
    jobject createIntegerObject(JNIEnv &env, jint value) {
        jclass int_class = env.FindClass("java/lang/Integer");
        jmethodID j_value_of = env.GetStaticMethodID(int_class, "valueOf", "(I)Ljava/lang/Integer;");

        return env.CallStaticObjectMethod(int_class, j_value_of, value);
    }

    jobject createLongObject(JNIEnv &env, jlong value) {
        jclass long_class = env.FindClass("java/lang/Long");
        jmethodID j_value_of = env.GetStaticMethodID(long_class, "valueOf", "(J)Ljava/lang/Long;");

        return env.CallStaticObjectMethod(long_class, j_value_of, value);
    }

    jobject createBooleanObject(JNIEnv &env, jboolean value) {
        jclass jBooleanClass = env.FindClass("java/lang/Boolean");
        jmethodID jBooleanValueOfMethod = env.GetStaticMethodID(jBooleanClass, "valueOf", "(B)Ljava/lang/Boolean;");

        return env.CallStaticObjectMethod(jBooleanClass, jBooleanValueOfMethod, value);
    }

    jobject createPair(JNIEnv &env, jobject first, jobject second) {
        namespace JPair = kotlin::Pair;

        return JPair::constructor0::invoke(env, first, second);
    }

    /**
      Activity is an jobject of type NativeDiscordActivity
    */
    DiscordActivity createDiscordActivity(JNIEnv &env, const jobject &jActivity) {
        namespace JDiscordActivity = gamesdk::impl::types::NativeDiscordActivity;

        jint type = JDiscordActivity::getType(env, jActivity);
        jlong application_id = JDiscordActivity::getApplicationId(env, jActivity);
        jstring name = JDiscordActivity::getName(env, jActivity);
        jstring state = JDiscordActivity::getState(env, jActivity);
        jstring details = JDiscordActivity::getDetails(env, jActivity);
        jlong timestamp_start = JDiscordActivity::getTimestampStart(env, jActivity);
        jlong timestamp_end = JDiscordActivity::getTimestampEnd(env, jActivity);
        jstring assets_large_image = JDiscordActivity::getAssetsLargeImage(env, jActivity);
        jstring assets_large_text = JDiscordActivity::getAssetsLargeText(env, jActivity);
        jstring assets_small_image = JDiscordActivity::getAssetsSmallImage(env, jActivity);
        jstring assets_small_text = JDiscordActivity::getAssetsSmallText(env, jActivity);
        jstring party_id = JDiscordActivity::getPartyId(env, jActivity);
        jint party_current_size = JDiscordActivity::getPartyCurrentSize(env, jActivity);
        jint party_max_size = JDiscordActivity::getPartyMaxSize(env, jActivity);
        jint party_privacy = JDiscordActivity::getPartyPrivacy(env, jActivity);
        jstring secrets_match = JDiscordActivity::getSecretsMatch(env, jActivity);
        jstring secrets_join = JDiscordActivity::getSecretsJoin(env, jActivity);
        jstring secrets_spectate = JDiscordActivity::getSecretsSpectate(env, jActivity);
        jboolean instance = JDiscordActivity::getInstance(env, jActivity);

        const char *name_native = env.GetStringUTFChars(name, nullptr);
        const char *state_native = env.GetStringUTFChars(state, nullptr);
        const char *details_native = env.GetStringUTFChars(details, nullptr);
        const char *assets_large_image_native = env.GetStringUTFChars(assets_large_image, nullptr);
        const char *assets_large_text_native = env.GetStringUTFChars(assets_large_text, nullptr);
        const char *assets_small_image_native = env.GetStringUTFChars(assets_small_image, nullptr);
        const char *assets_small_text_native = env.GetStringUTFChars(assets_small_text, nullptr);
        const char *party_id_native = env.GetStringUTFChars(party_id, nullptr);
        const char *secrets_match_native = env.GetStringUTFChars(secrets_match, nullptr);
        const char *secrets_join_native = env.GetStringUTFChars(secrets_join, nullptr);
        const char *secrets_spectate_native = env.GetStringUTFChars(secrets_spectate, nullptr);

        DiscordActivity activity{};
        activity.type = (EDiscordActivityType) type;
        activity.application_id = application_id;

        //TODO: Check if the length of an utf8 char * is checked beforehand
        strncpy(activity.name, name_native, sizeof(activity.name));
        strncpy(activity.state, state_native, sizeof(activity.state));
        strncpy(activity.details, details_native, sizeof(activity.details));

        activity.timestamps.start = timestamp_start;
        activity.timestamps.end = timestamp_end;

        strncpy(activity.assets.large_image, assets_large_image_native, sizeof(activity.assets.large_image));
        strncpy(activity.assets.large_text, assets_large_text_native, sizeof(activity.assets.large_text));
        strncpy(activity.assets.small_image, assets_small_image_native, sizeof(activity.assets.small_image));
        strncpy(activity.assets.small_text, assets_small_text_native, sizeof(activity.assets.small_text));

        strncpy(activity.party.id, party_id_native, sizeof(activity.party.id));
        activity.party.size.current_size = party_current_size;
        activity.party.size.max_size = party_max_size;
        activity.party.privacy = (EDiscordActivityPartyPrivacy) party_privacy;

        strncpy(activity.secrets.match, secrets_match_native, sizeof(activity.secrets.match));
        strncpy(activity.secrets.join, secrets_join_native, sizeof(activity.secrets.join));
        strncpy(activity.secrets.spectate, secrets_spectate_native, sizeof(activity.secrets.spectate));

        activity.instance = instance;

        env.ReleaseStringUTFChars(name, name_native);
        env.ReleaseStringUTFChars(state, state_native);
        env.ReleaseStringUTFChars(details, details_native);

        env.ReleaseStringUTFChars(assets_large_image, assets_large_image_native);
        env.ReleaseStringUTFChars(assets_large_text, assets_large_text_native);
        env.ReleaseStringUTFChars(assets_small_image, assets_small_image_native);
        env.ReleaseStringUTFChars(assets_small_text, assets_small_text_native);

        env.ReleaseStringUTFChars(party_id, party_id_native);

        env.ReleaseStringUTFChars(secrets_match, secrets_match_native);
        env.ReleaseStringUTFChars(secrets_join, secrets_join_native);
        env.ReleaseStringUTFChars(secrets_spectate, secrets_spectate_native);

        return activity;
    }

    jobject createJavaActivity(JNIEnv &env, const DiscordActivity &activity) {
        auto jType = (jint) activity.type;
        auto jApplicationId = (jlong) activity.application_id;
        auto jName = env.NewStringUTF(activity.name);
        auto jState = env.NewStringUTF(activity.state);
        auto jDetails = env.NewStringUTF(activity.details);

        auto jTimestampStart = (jlong) activity.timestamps.start;
        auto jTimestampEnd = (jlong) activity.timestamps.end;

        auto jAssetLargeImage = env.NewStringUTF(activity.assets.large_image);
        auto jAssetLargeText = env.NewStringUTF(activity.assets.large_text);
        auto jAssetSmallImage = env.NewStringUTF(activity.assets.small_image);
        auto jAssetSmallText = env.NewStringUTF(activity.assets.small_text);

        auto jPartyId = env.NewStringUTF(activity.party.id);
        auto jPartySizeCurrent = (jint) activity.party.size.current_size;
        auto jPartySizeMax = (jint) activity.party.size.max_size;
        auto jPartyPrivacy = (jint) activity.party.privacy;

        auto jSecretMatch = env.NewStringUTF(activity.secrets.match);
        auto jSecretJoin = env.NewStringUTF(activity.secrets.join);
        auto jSecretSpectate = env.NewStringUTF(activity.secrets.spectate);

        auto instance = (jboolean) activity.instance;

        namespace JDiscordActivity = gamesdk::impl::types::NativeDiscordActivity;

        return JDiscordActivity::constructor0::invoke(
                env,
                jType, jApplicationId, jName, jState, jDetails, jTimestampStart,
                jTimestampEnd, jAssetLargeImage, jAssetLargeText, jAssetSmallImage,
                jAssetSmallText, jPartyId, jPartySizeCurrent, jPartySizeMax, jPartyPrivacy,
                jSecretMatch, jSecretJoin, jSecretSpectate, instance);
    }

    jobject createJavaUser(JNIEnv &env, const DiscordUser &user) {
        auto jId = (jlong) user.id;
        auto jUsername = env.NewStringUTF(user.username);
        auto jDiscriminator = env.NewStringUTF(user.discriminator);
        auto jAvatar = env.NewStringUTF(user.avatar);
        auto jBot = (jboolean) user.bot;

        namespace JUser = gamesdk::api::types::DiscordUser;

        return JUser::constructor0::invoke(env, jId, jUsername, jDiscriminator, jAvatar, jBot);
    }

    jobject createJavaPresence(JNIEnv &env, const DiscordPresence &presence) {
        auto jStatus = (jint) presence.status;
        auto jActivity = createJavaActivity(env, presence.activity);

        namespace JPresence = gamesdk::impl::types::NativeDiscordPresence;

        return JPresence::constructor0::invoke(env, jStatus, jActivity);
    }

    jobject createJavaRelationship(JNIEnv &env, const DiscordRelationship &relationship) {
        jint jType = (jint) relationship.type;
        jobject jUser = createJavaUser(env, relationship.user);
        jobject jPresence = createJavaPresence(env, relationship.presence);

        namespace JRelationship = gamesdk::impl::types::NativeDiscordRelationship;

        return JRelationship::constructor0::invoke(env, jType, jUser, jPresence);
    }

    jobject createNativeDiscordObjectResultSuccess(JNIEnv &env, jobject object) {
        namespace JSuccess = gamesdk::impl::NativeDiscordObjectResult::Success;

        return JSuccess::constructor0::invoke(env, object);
    }

    jobject createNativeDiscordObjectResultFailure(JNIEnv &env, EDiscordResult result) {
        namespace JFailure = gamesdk::impl::NativeDiscordObjectResult::Failure;

        return JFailure::constructor0::invoke(env, (jint) result);
    }

    jobject createJavaOAuth2Token(JNIEnv &env, jstring access_token, jstring scopes, jlong expires) {
        jclass oauth2_token_class = env.FindClass("com/almightyalpaca/jetbrains/plugins/discord/gamesdk/api/DiscordOAuth2Token");
        jmethodID constructor = env.GetMethodID(oauth2_token_class, "<init>", "(Ljava/lang/String;Ljava/lang/String;J)V");
        jobject token = env.NewObject(oauth2_token_class, constructor, access_token, scopes, expires);

        return token;
    }
} // namespace types
