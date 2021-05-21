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

#include <limits>

#include "jniclasses.h"

namespace types {
    std::string createNativeString(JNIEnv &env, jbyteArray bytes) {
        auto length = env.GetArrayLength(bytes);

        std::string target(length, ' '); // TODO: Is a space the best idea here? Probably doesn't matter though
        target.reserve(length);

        env.GetByteArrayRegion(bytes, 0, length, (jbyte *) target.data());

        return target;
    }

    jbyteArray createJavaString(JNIEnv &env, const char *string) {
        auto length = strlen(string);

        if (length > std::numeric_limits<jint>::max()) {
            return nullptr;
        }

        jbyteArray bytes = env.NewByteArray((jint) length);
        env.SetByteArrayRegion(bytes, 0, (jint) length, (jbyte *) string);

        return bytes;
    }

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

    /**
      Activity is an jobject of type NativeDiscordActivity
    */
    DiscordActivity createDiscordActivity(JNIEnv &env, const jobject &jActivity) {
        namespace JDiscordActivity = gamesdk::impl::types::NativeDiscordActivity;

        DiscordActivity activity{};
        activity.type = (EDiscordActivityType) JDiscordActivity::getType(env, jActivity);
        activity.application_id = JDiscordActivity::getApplicationId(env, jActivity);

        createNativeString<128>(env, JDiscordActivity::getName(env, jActivity), activity.name);
        createNativeString<128>(env, JDiscordActivity::getState(env, jActivity), activity.state);
        createNativeString<128>(env, JDiscordActivity::getDetails(env, jActivity), activity.details);

        activity.timestamps.start = JDiscordActivity::getTimestampStart(env, jActivity);
        activity.timestamps.end = JDiscordActivity::getTimestampEnd(env, jActivity);

        createNativeString<128>(env, JDiscordActivity::getAssetsLargeImage(env, jActivity), activity.assets.large_image);
        createNativeString<128>(env, JDiscordActivity::getAssetsLargeText(env, jActivity), activity.assets.large_text);
        createNativeString<128>(env, JDiscordActivity::getAssetsSmallImage(env, jActivity), activity.assets.small_image);
        createNativeString<128>(env, JDiscordActivity::getAssetsSmallText(env, jActivity), activity.assets.small_text);

        createNativeString<128>(env, JDiscordActivity::getPartyId(env, jActivity), activity.party.id);
        activity.party.size.current_size = JDiscordActivity::getPartyCurrentSize(env, jActivity);
        activity.party.size.max_size = JDiscordActivity::getPartyMaxSize(env, jActivity);
        activity.party.privacy = (EDiscordActivityPartyPrivacy) JDiscordActivity::getPartyPrivacy(env, jActivity);

        createNativeString<128>(env, JDiscordActivity::getSecretsMatch(env, jActivity), activity.secrets.match);
        createNativeString<128>(env, JDiscordActivity::getSecretsJoin(env, jActivity), activity.secrets.join);
        createNativeString<128>(env, JDiscordActivity::getSecretsSpectate(env, jActivity), activity.secrets.spectate);

        activity.instance = JDiscordActivity::getInstance(env, jActivity);

        return activity;
    }

    jobject createJavaActivity(JNIEnv &env, const DiscordActivity &activity) {
        auto jType = (jint) activity.type;
        auto jApplicationId = (jlong) activity.application_id;
        auto jName = createJavaString(env, activity.name);
        auto jState = createJavaString(env, activity.state);
        auto jDetails = createJavaString(env, activity.details);

        auto jTimestampStart = (jlong) activity.timestamps.start;
        auto jTimestampEnd = (jlong) activity.timestamps.end;

        auto jAssetLargeImage = createJavaString(env, activity.assets.large_image);
        auto jAssetLargeText = createJavaString(env, activity.assets.large_text);
        auto jAssetSmallImage = createJavaString(env, activity.assets.small_image);
        auto jAssetSmallText = createJavaString(env, activity.assets.small_text);

        auto jPartyId = createJavaString(env, activity.party.id);
        auto jPartySizeCurrent = (jint) activity.party.size.current_size;
        auto jPartySizeMax = (jint) activity.party.size.max_size;
        auto jPartyPrivacy = (jint) activity.party.privacy;

        auto jSecretMatch = createJavaString(env, activity.secrets.match);
        auto jSecretJoin = createJavaString(env, activity.secrets.join);
        auto jSecretSpectate = createJavaString(env, activity.secrets.spectate);

        auto instance = (jboolean) activity.instance;

        namespace JDiscordActivity = gamesdk::impl::types::NativeDiscordActivity;

        return JDiscordActivity::constructor0::invoke(
                env,
                jType, jApplicationId, jName, jState, jDetails, jTimestampStart,
                jTimestampEnd, jAssetLargeImage, jAssetLargeText, jAssetSmallImage,
                jAssetSmallText, jPartyId, jPartySizeCurrent, jPartySizeMax, jPartyPrivacy,
                jSecretMatch, jSecretJoin, jSecretSpectate, instance);
    }

    jobject createJavaImageDimensions(JNIEnv &env, const DiscordImageDimensions &dimensions) {
        auto jWidth = (jint) dimensions.width;
        auto jHeight = (jint) dimensions.height;

        namespace JDiscordImageDimensions = gamesdk::impl::types::NativeDiscordImageDimensions;

        return JDiscordImageDimensions::constructor0::invoke(env, jWidth, jHeight, nullptr); // TODO: WTF
    }

    DiscordImageHandle createDiscordImageHandle(JNIEnv &env, const jobject &jHandle) {
        namespace JDiscordImageHandle = gamesdk::impl::types::NativeDiscordImageHandle;

        DiscordImageHandle handle{};
        handle.type = (EDiscordImageType) JDiscordImageHandle::getType(env, jHandle);
        handle.id = JDiscordImageHandle::getId(env, jHandle);
        handle.size = JDiscordImageHandle::getSize(env, jHandle);

        return handle;
    }

    jobject createJavaImageHandle(JNIEnv &env, const DiscordImageHandle &handle) {
        auto jType = (jint) handle.type;
        auto jId = (jlong) handle.id;
        auto jSize = (jint) handle.size;

        namespace JDiscordImageHandle = gamesdk::impl::types::NativeDiscordImageHandle;

        return JDiscordImageHandle::constructor0::invoke(env, jType, jId, jSize, nullptr); // TODO: WTF
    }

    jobject createJavaUser(JNIEnv &env, const DiscordUser &user) {
        auto jId = (jlong) user.id;
        auto jUsername = createJavaString(env, user.username);
        auto jDiscriminator = createJavaString(env, user.discriminator);
        auto jAvatar = createJavaString(env, user.avatar);
        auto jBot = (jboolean) user.bot;

        namespace JUser = gamesdk::impl::types::NativeDiscordUser;

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

    jobject createJavaOAuth2Token(JNIEnv &env, DiscordOAuth2Token &token) {
        auto jAccessToken = createJavaString(env, token.access_token);
        auto jScopes = createJavaString(env, token.scopes);
        auto jExpires = (jlong) token.expires;

        namespace JDiscordOAuth2Token = gamesdk::impl::types::NativeDiscordOAuth2Token;

        return JDiscordOAuth2Token::constructor0::invoke(env, jAccessToken, jScopes, jExpires);
    }

    jobject createNativeDiscordObjectResultSuccess(JNIEnv &env, jobject object) {
        namespace JSuccess = gamesdk::impl::NativeDiscordObjectResult::Success;

        return JSuccess::constructor0::invoke(env, object);
    }

    jobject createNativeDiscordObjectResultFailure(JNIEnv &env, EDiscordResult result) {
        namespace JFailure = gamesdk::impl::NativeDiscordObjectResult::Failure;

        return JFailure::constructor0::invoke(env, (jint) result);
    }

    jobject createNativeDiscordObjectResult(JNIEnv &env, enum EDiscordResult result, jobject object) {
        if (result == DiscordResult_Ok) {
            return createNativeDiscordObjectResultSuccess(env, object);
        } else {
            return createNativeDiscordObjectResultFailure(env, result);
        }
    }
} // namespace types
