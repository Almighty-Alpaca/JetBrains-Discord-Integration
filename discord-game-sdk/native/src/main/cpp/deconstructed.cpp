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

#include "deconstructed.h"

namespace deconstructed
{
    /**
      Activity is an jobject of type DeconstructedDiscordActivity
    */
    struct DiscordActivity constructActivity(JNIEnv* env, jobject jActivity) {
        jclass      discord_activity_class      = env->GetObjectClass(jActivity);

        // type signatures: https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html#type_signatures
        jfieldID    type_field_id               = env->GetFieldID(discord_activity_class, "type", "I"),
                    application_id_field_id     = env->GetFieldID(discord_activity_class, "applicationId", "J"),
                    name_field_id               = env->GetFieldID(discord_activity_class, "name", "Ljava/lang/String;"),
                    state_field_id              = env->GetFieldID(discord_activity_class, "state", "Ljava/lang/String;"),
                    details_field_id            = env->GetFieldID(discord_activity_class, "details", "Ljava/lang/String;"),
                    timestamp_start_field_id    = env->GetFieldID(discord_activity_class, "timestampStart", "J"),
                    timestamp_end_field_id      = env->GetFieldID(discord_activity_class, "timestampEnd", "J"),
                    assets_large_image_field_id = env->GetFieldID(discord_activity_class, "assetsLargeImage", "Ljava/lang/String;"),
                    assets_large_text_field_id  = env->GetFieldID(discord_activity_class, "assetsLargeText", "Ljava/lang/String;"),
                    assets_small_image_field_id = env->GetFieldID(discord_activity_class, "assetsSmallImage", "Ljava/lang/String;"),
                    assets_small_text_field_id  = env->GetFieldID(discord_activity_class, "assetsSmallText", "Ljava/lang/String;"),
                    party_id_field_id           = env->GetFieldID(discord_activity_class, "partyId", "Ljava/lang/String;"),
                    party_current_size_field_id = env->GetFieldID(discord_activity_class, "partyCurrentSize", "I"),
                    party_max_size_field_id     = env->GetFieldID(discord_activity_class, "partyMaxSize", "I"),
                    party_privacy_field_id      = env->GetFieldID(discord_activity_class, "partyPrivacy", "I"),
                    secrets_match_field_id      = env->GetFieldID(discord_activity_class, "secretsMatch", "Ljava/lang/String;"),
                    secrets_join_field_id       = env->GetFieldID(discord_activity_class, "secretsJoin", "Ljava/lang/String;"),
                    secrets_spectate_field_id   = env->GetFieldID(discord_activity_class, "secretsSpectate", "Ljava/lang/String;"),
                    instance_field_id           = env->GetFieldID(discord_activity_class, "instance", "Z");

        jint        type                        = env->GetIntField(jActivity, type_field_id);
        jlong       application_id              = env->GetLongField(jActivity, application_id_field_id);
        auto        name                        = (jstring) env->GetObjectField(jActivity, name_field_id);
        auto        state                       = (jstring) env->GetObjectField(jActivity, state_field_id);
        auto        details                     = (jstring) env->GetObjectField(jActivity, details_field_id);
        jlong       timestamp_start             = env->GetLongField(jActivity, timestamp_start_field_id);
        jlong       timestamp_end               = env->GetLongField(jActivity, timestamp_end_field_id);
        auto        assets_large_image          = (jstring) env->GetObjectField(jActivity, assets_large_image_field_id);
        auto        assets_large_text           = (jstring) env->GetObjectField(jActivity, assets_large_text_field_id);
        auto        assets_small_image          = (jstring) env->GetObjectField(jActivity, assets_small_image_field_id);
        auto        assets_small_text           = (jstring) env->GetObjectField(jActivity, assets_small_text_field_id);
        auto        party_id                    = (jstring) env->GetObjectField(jActivity, party_id_field_id);
        jint        party_current_size          = env->GetIntField(jActivity, party_current_size_field_id);
        jint        party_max_size              = env->GetIntField(jActivity, party_max_size_field_id);
        jint        party_privacy               = env->GetIntField(jActivity, party_privacy_field_id);
        auto        secrets_match               = (jstring) env->GetObjectField(jActivity, secrets_match_field_id);
        auto        secrets_join                = (jstring) env->GetObjectField(jActivity, secrets_join_field_id);
        auto        secrets_spectate            = (jstring) env->GetObjectField(jActivity, secrets_spectate_field_id);
        bool        instance                    = env->GetBooleanField (jActivity, instance_field_id);

        const char* name_native                 = env->GetStringUTFChars(name, nullptr);
        const char* state_native                = env->GetStringUTFChars(state, nullptr);
        const char* details_native              = env->GetStringUTFChars(details, nullptr);
        const char* assets_large_image_native   = env->GetStringUTFChars(assets_large_image, nullptr);
        const char* assets_large_text_native    = env->GetStringUTFChars(assets_large_text, nullptr);
        const char* assets_small_image_native   = env->GetStringUTFChars(assets_small_image, nullptr);
        const char* assets_small_text_native    = env->GetStringUTFChars(assets_small_text, nullptr);
        const char* party_id_native             = env->GetStringUTFChars(party_id, nullptr);
        const char* secrets_match_native        = env->GetStringUTFChars(secrets_match, nullptr);
        const char* secrets_join_native         = env->GetStringUTFChars(secrets_join, nullptr);
        const char* secrets_spectate_native     = env->GetStringUTFChars(secrets_spectate, nullptr);

        struct DiscordActivity activity{};
        activity.type = (EDiscordActivityType)type;
        activity.application_id = application_id;

        strncpy(activity.name,      name_native,    sizeof(activity.name));
        strncpy(activity.state,     state_native,   sizeof(activity.state));
        strncpy(activity.details,   details_native, sizeof(activity.details));

        activity.timestamps.start = timestamp_start;
        activity.timestamps.end   = timestamp_end;

        strncpy(activity.assets.large_image,      assets_large_image_native,    sizeof(activity.assets.large_image));
        strncpy(activity.assets.large_text,       assets_large_text_native,     sizeof(activity.assets.large_text));
        strncpy(activity.assets.small_image,      assets_small_image_native,    sizeof(activity.assets.small_image));
        strncpy(activity.assets.small_text,       assets_small_text_native,     sizeof(activity.assets.small_text));

        strncpy(activity.party.id, party_id_native, sizeof(activity.party.id));
        activity.party.size.current_size = party_current_size;
        activity.party.size.max_size     = party_max_size;
        activity.party.privacy           = (EDiscordActivityPartyPrivacy) party_privacy;

        strncpy(activity.secrets.match,     secrets_match_native,    sizeof(activity.secrets.match));
        strncpy(activity.secrets.join,      secrets_join_native,     sizeof(activity.secrets.join));
        strncpy(activity.secrets.spectate,  secrets_spectate_native, sizeof(activity.secrets.spectate));

        activity.instance = instance;

        env->ReleaseStringUTFChars(name,    name_native);
        env->ReleaseStringUTFChars(state,   state_native);
        env->ReleaseStringUTFChars(details, details_native);

        env->ReleaseStringUTFChars(assets_large_image,  assets_large_image_native);
        env->ReleaseStringUTFChars(assets_large_text,   assets_large_text_native);
        env->ReleaseStringUTFChars(assets_small_image,  assets_small_image_native);
        env->ReleaseStringUTFChars(assets_small_text,   assets_small_text_native);

        env->ReleaseStringUTFChars(party_id, party_id_native);

        env->ReleaseStringUTFChars(secrets_match,    secrets_match_native);
        env->ReleaseStringUTFChars(secrets_join,     secrets_join_native);
        env->ReleaseStringUTFChars(secrets_spectate, secrets_spectate_native);

        return activity;
    }
} // namespace deconstructed
