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
        jclass pair_class = env.FindClass("kotlin/Pair");
        jmethodID constructor = env.GetMethodID(pair_class, "<init>", "(Ljava/lang/Object;Ljava/lang/Object;)V");
        jobject pair = env.NewObject(pair_class, constructor, first, second);

        return pair;
    }

    /**
      Activity is an jobject of type NativeDiscordActivity
    */
    DiscordActivity createDiscordActivity(JNIEnv &env, jobject &jActivity) {
        jclass discord_activity_class = env.GetObjectClass(jActivity);

        // type signatures: https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html#type_signatures
        jfieldID
                type_field_id = env.GetFieldID(discord_activity_class, "type", "I"),
                application_id_field_id = env.GetFieldID(discord_activity_class, "applicationId", "J"),
                name_field_id = env.GetFieldID(discord_activity_class, "name", "Ljava/lang/String;"),
                state_field_id = env.GetFieldID(discord_activity_class, "state", "Ljava/lang/String;"),
                details_field_id = env.GetFieldID(discord_activity_class, "details", "Ljava/lang/String;"),
                timestamp_start_field_id = env.GetFieldID(discord_activity_class, "timestampStart", "J"),
                timestamp_end_field_id = env.GetFieldID(discord_activity_class, "timestampEnd", "J"),
                assets_large_image_field_id = env.GetFieldID(discord_activity_class, "assetsLargeImage", "Ljava/lang/String;"),
                assets_large_text_field_id = env.GetFieldID(discord_activity_class, "assetsLargeText", "Ljava/lang/String;"),
                assets_small_image_field_id = env.GetFieldID(discord_activity_class, "assetsSmallImage", "Ljava/lang/String;"),
                assets_small_text_field_id = env.GetFieldID(discord_activity_class, "assetsSmallText", "Ljava/lang/String;"),
                party_id_field_id = env.GetFieldID(discord_activity_class, "partyId", "Ljava/lang/String;"),
                party_current_size_field_id = env.GetFieldID(discord_activity_class, "partyCurrentSize", "I"),
                party_max_size_field_id = env.GetFieldID(discord_activity_class, "partyMaxSize", "I"),
                party_privacy_field_id = env.GetFieldID(discord_activity_class, "partyPrivacy", "I"),
                secrets_match_field_id = env.GetFieldID(discord_activity_class, "secretsMatch", "Ljava/lang/String;"),
                secrets_join_field_id = env.GetFieldID(discord_activity_class, "secretsJoin", "Ljava/lang/String;"),
                secrets_spectate_field_id = env.GetFieldID(discord_activity_class, "secretsSpectate", "Ljava/lang/String;"),
                instance_field_id = env.GetFieldID(discord_activity_class, "instance", "Z");

        jint type = env.GetIntField(jActivity, type_field_id);
        jlong application_id = env.GetLongField(jActivity, application_id_field_id);
        jstring name = (jstring) env.GetObjectField(jActivity, name_field_id);
        jstring state = (jstring) env.GetObjectField(jActivity, state_field_id);
        jstring details = (jstring) env.GetObjectField(jActivity, details_field_id);
        jlong timestamp_start = env.GetLongField(jActivity, timestamp_start_field_id);
        jlong timestamp_end = env.GetLongField(jActivity, timestamp_end_field_id);
        jstring assets_large_image = (jstring) env.GetObjectField(jActivity, assets_large_image_field_id);
        jstring assets_large_text = (jstring) env.GetObjectField(jActivity, assets_large_text_field_id);
        jstring assets_small_image = (jstring) env.GetObjectField(jActivity, assets_small_image_field_id);
        jstring assets_small_text = (jstring) env.GetObjectField(jActivity, assets_small_text_field_id);
        jstring party_id = (jstring) env.GetObjectField(jActivity, party_id_field_id);
        jint party_current_size = env.GetIntField(jActivity, party_current_size_field_id);
        jint party_max_size = env.GetIntField(jActivity, party_max_size_field_id);
        jint party_privacy = env.GetIntField(jActivity, party_privacy_field_id);
        jstring secrets_match = (jstring) env.GetObjectField(jActivity, secrets_match_field_id);
        jstring secrets_join = (jstring) env.GetObjectField(jActivity, secrets_join_field_id);
        jstring secrets_spectate = (jstring) env.GetObjectField(jActivity, secrets_spectate_field_id);
        bool instance = env.GetBooleanField(jActivity, instance_field_id);

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

        strncpy_s(activity.name, name_native, sizeof(activity.name));
        strncpy_s(activity.state, state_native, sizeof(activity.state));
        strncpy_s(activity.details, details_native, sizeof(activity.details));

        activity.timestamps.start = timestamp_start;
        activity.timestamps.end = timestamp_end;

        strncpy_s(activity.assets.large_image, assets_large_image_native, sizeof(activity.assets.large_image));
        strncpy_s(activity.assets.large_text, assets_large_text_native, sizeof(activity.assets.large_text));
        strncpy_s(activity.assets.small_image, assets_small_image_native, sizeof(activity.assets.small_image));
        strncpy_s(activity.assets.small_text, assets_small_text_native, sizeof(activity.assets.small_text));

        strncpy_s(activity.party.id, party_id_native, sizeof(activity.party.id));
        activity.party.size.current_size = party_current_size;
        activity.party.size.max_size = party_max_size;
        activity.party.privacy = (EDiscordActivityPartyPrivacy) party_privacy;

        strncpy_s(activity.secrets.match, secrets_match_native, sizeof(activity.secrets.match));
        strncpy_s(activity.secrets.join, secrets_join_native, sizeof(activity.secrets.join));
        strncpy_s(activity.secrets.spectate, secrets_spectate_native, sizeof(activity.secrets.spectate));

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

    jobject createJavaActivity(JNIEnv &env, DiscordActivity &activity) {
        jclass jActivityClass = env.FindClass("gamesdk/impl/types/NativeDiscordActivity");

        static const auto jActivitySignature = "(IJLjava/lang/String;Ljava/lang/String;Ljava/lang/String;JJLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IIILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V";
        jmethodID jActivityConstructor = env.GetMethodID(jActivityClass, "<init>", jActivitySignature);

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

        return env.NewObject(jActivityClass, jActivityConstructor,
                             jType, jApplicationId, jName, jState, jDetails, jTimestampStart, jTimestampEnd, jAssetLargeImage, jAssetLargeText, jAssetSmallImage,
                             jAssetSmallText, jPartyId, jPartySizeCurrent, jPartySizeMax, jPartyPrivacy, jSecretMatch, jSecretJoin, jSecretSpectate, instance);
    }

    jobject createJavaUser(JNIEnv &env, DiscordUser &user) {
        jclass jUserClass = env.FindClass("gamesdk/api/types/DiscordUser");
        jmethodID jUserConstructor = env.GetMethodID(jUserClass, "<init>", "(JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V");

        auto jId = (jint) user.id;
        auto jUsername = env.NewStringUTF(user.username);
        auto jDiscriminator = env.NewStringUTF(user.discriminator);
        auto jAvatar = env.NewStringUTF(user.avatar);
        auto jBot = (jboolean) user.bot;

        return env.NewObject(jUserClass, jUserConstructor, jId, jUsername, jDiscriminator, jAvatar, jBot);
    }

    jobject createJavaPresence(JNIEnv &env, DiscordPresence &presence) {
        jclass jPresenceClass = env.FindClass("gamesdk/impl/types/NativeDiscordPresence");
        jmethodID jPresenceConstructor = env.GetMethodID(jPresenceClass, "<init>", "(ILgamesdk/impl/types/NativeDiscordActivity;)V");

        auto jStatus = (jint) presence.status;
        auto jActivity = createJavaActivity(env, presence.activity);

        return env.NewObject(jPresenceClass, jPresenceConstructor, jStatus, jActivity);
    }

    jobject createJavaRelationship(JNIEnv &env, DiscordRelationship &relationship) {
        jint jType = (jint) relationship.type;
        jobject jUser = createJavaUser(env, relationship.user);
        jobject jPresence = createJavaPresence(env, relationship.presence);

        jclass jRelationshipClass = env.FindClass("gamesdk/impl/types/NativeDiscordRelationship");

        jmethodID jRelationshipConstructor = env.GetMethodID(jRelationshipClass, "<init>", "(ILgamesdk/api/types/DiscordUser;Lgamesdk/impl/types/NativeDiscordPresence;)V");

        jobject jRelationship = env.NewObject(jRelationshipClass, jRelationshipConstructor, jType, jUser, jPresence);

        return jRelationship;
    }

    jobject createNativeDiscordObjectResult(JNIEnv &env, EDiscordResult result, jobject object) {
        if (result == DiscordResult_Ok) {
            jclass jSuccessClass = env.FindClass("gamesdk/impl/NativeDiscordObjectResult$Success");
            jmethodID jSuccessConstructor = env.GetMethodID(jSuccessClass, "<init>", "(Ljava/lang/Object;)V");
            return env.NewObject(jSuccessClass, jSuccessConstructor, object);
        } else {
            jclass jFailureClass = env.FindClass("gamesdk/impl/NativeDiscordObjectResult$Failure");
            jmethodID jFailureConstructor = env.GetMethodID(jFailureClass, "<init>", "(I)V");
            return env.NewObject(jFailureClass, jFailureConstructor, (jint) result);
        }
    }
} // namespace types
