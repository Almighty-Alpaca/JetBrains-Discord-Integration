#include "com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl.h"
#include "discord_game_sdk.h"
#include "commons.h"

IDiscordActivityEvents       activity_manager_events;

/**
  Activity is an jobject of type DeconstructedDiscordActivity
 */
static DiscordActivity construct_activity(JNIEnv* env, jobject p_activity) {
    jclass      discord_activity_class      = env->GetObjectClass(p_activity);

    /// type signatures: https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html#type_signatures
    jfieldID    type_field_id               = env->GetFieldID(discord_activity_class, "type", "I"),
                application_id_field_id     = env->GetFieldID(discord_activity_class, "applicationId", "J"),
                name_field_id               = env->GetFieldID(discord_activity_class, "name", "Ljava.lang.String;"),
                state_field_id              = env->GetFieldID(discord_activity_class, "state", "Ljava.lang.String;"),
                details_field_id            = env->GetFieldID(discord_activity_class, "details", "Ljava.lang.String;"),
                timestamp_start_field_id    = env->GetFieldID(discord_activity_class, "timestampStart", "J"),
                timestamp_end_field_id      = env->GetFieldID(discord_activity_class, "timestampEnd", "J"),
                assets_large_image_field_id = env->GetFieldID(discord_activity_class, "assetsLargeImage", "Ljava.lang.String;"),
                assets_large_text_field_id  = env->GetFieldID(discord_activity_class, "assetsLargeText", "Ljava.lang.String;"),
                assets_small_image_field_id = env->GetFieldID(discord_activity_class, "assetsSmallImage", "Ljava.lang.String;"),
                assets_small_text_field_id  = env->GetFieldID(discord_activity_class, "assetsSmallText", "Ljava.lang.String;"),
                party_id_field_id           = env->GetFieldID(discord_activity_class, "partyId", "Ljava.lang.String;"),
                party_current_size_field_id = env->GetFieldID(discord_activity_class, "partyCurrentSize", "I"),
                party_max_size_field_id     = env->GetFieldID(discord_activity_class, "partyMaxSize", "I"),
                secrets_match_field_id      = env->GetFieldID(discord_activity_class, "secretsMatch", "Ljava.lang.String;"),
                secrets_join_field_id       = env->GetFieldID(discord_activity_class, "secretsJoin", "Ljava.lang.String;"),
                secrets_spectate_field_id   = env->GetFieldID(discord_activity_class, "secretsSpectate", "Ljava.lang.String;"),
                instance_field_id           = env->GetFieldID(discord_activity_class, "instance", "Z");

    jint        type                        = env->GetIntField(p_activity, type_field_id);
    jlong       application_id              = env->GetLongField(p_activity, application_id_field_id);
    jstring     name                        = (jstring) env->GetObjectField(p_activity, name_field_id);
    jstring     state                       = (jstring) env->GetObjectField(p_activity, state_field_id);
    jstring     details                     = (jstring) env->GetObjectField(p_activity, details_field_id);
    jlong       timestamp_start             = env->GetLongField(p_activity, timestamp_start_field_id);
    jlong       timestamp_end               = env->GetLongField(p_activity, timestamp_end_field_id);
    jstring     assets_large_image          = (jstring) env->GetObjectField(p_activity, assets_large_image_field_id);
    jstring     assets_large_text           = (jstring) env->GetObjectField(p_activity, assets_large_text_field_id);
    jstring     assets_small_image          = (jstring) env->GetObjectField(p_activity, assets_small_image_field_id);
    jstring     assets_small_text           = (jstring) env->GetObjectField(p_activity, assets_small_text_field_id);
    jstring     party_id                    = (jstring) env->GetObjectField(p_activity, party_id_field_id);
    jint        party_current_size          = env->GetIntField(p_activity, party_current_size_field_id);
    jint        party_max_size              = env->GetIntField(p_activity, party_max_size_field_id);
    jstring     secrets_match               = (jstring) env->GetObjectField(p_activity, secrets_match_field_id);
    jstring     secrets_join                = (jstring) env->GetObjectField(p_activity, secrets_join_field_id);
    jstring     secrets_spectate            = (jstring) env->GetObjectField(p_activity, secrets_spectate_field_id);
    bool        instance                    = env->GetBooleanField (p_activity, instance_field_id);


    const char* name_native                 = env->GetStringUTFChars(name, NULL);
    const char* state_native                = env->GetStringUTFChars(state, NULL);
    const char* details_native              = env->GetStringUTFChars(details, NULL);
    const char* assets_large_image_native   = env->GetStringUTFChars(assets_large_image, NULL);
    const char* assets_large_text_native    = env->GetStringUTFChars(assets_large_text, NULL);
    const char* assets_small_image_native   = env->GetStringUTFChars(assets_small_image, NULL);
    const char* assets_small_text_native    = env->GetStringUTFChars(assets_small_text, NULL);
    const char* party_id_native             = env->GetStringUTFChars(party_id, NULL);
    const char* secrets_match_native        = env->GetStringUTFChars(secrets_match, NULL);
    const char* secrets_join_native         = env->GetStringUTFChars(secrets_match, NULL);
    const char* secrets_spectate_native     = env->GetStringUTFChars(secrets_match, NULL);

    DiscordActivity activity;
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

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl
 * Method:     native_1registerCommand
 * Signature:  (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl_native_1registerCommand
  (JNIEnv * env, jobject this_ptr, jstring name)
{
    IDiscordActivityManager* manager;
    GET_INTERFACE_PTR(env, this_ptr, "DiscordActivityManagerImpl", IDiscordActivityManager, manager);
    const char* nameNative = env->GetStringUTFChars(name, 0);

    int return_code = manager->register_command(manager, nameNative);

    env->ReleaseStringUTFChars(name, nameNative);

    return return_code;
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl
 * Method:     native_1registerSteam_0002dWZ4Q5Ns
 * Signature:  (I)I
 */
JNIEXPORT jint JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl_native_1registerSteam_0002dWZ4Q5Ns
  (JNIEnv * env, jobject this_ptr, jint steam_id)
{
    IDiscordActivityManager* manager;
    GET_INTERFACE_PTR(env, this_ptr, "DiscordActivityManagerImpl", IDiscordActivityManager, manager);

    return manager->register_steam(manager, steam_id);
}

struct native_callback_data {
    JNIEnv* env;
    jobject callback;
};

static void callback_activity_manager(void* vp_cb_data, EDiscordResult result){
    native_callback_data* cb_data = (native_callback_data*) vp_cb_data;
    jclass callback_class;
    callback_class = cb_data->env->GetObjectClass(cb_data->callback);
    jmethodID invoke_method_id = cb_data->env->GetMethodID(callback_class, "invoke", "(I)V");
    cb_data->env->CallObjectMethod(cb_data->callback, invoke_method_id, result);
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl
 * Method:     native_1updateActivity
 * Signature:  (Lcom/almightyalpaca/jetbrains/plugins/discord/gamesdk/DeconstructedDiscordActivity;Ljava/lang/Object;Lkotlin/jvm/functions/Function2;)V
 */
JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl_native_1updateActivity
  (JNIEnv *env, jobject this_ptr, jobject p_activity, jobject p_callback)
{
    IDiscordActivityManager* manager;
    GET_INTERFACE_PTR(env, this_ptr, "DiscordActivityManagerImpl", IDiscordActivityManager, manager);

    DiscordActivity activity = construct_activity(env, p_activity);

    native_callback_data ncb_data = {.env = env, .callback = p_callback};

    manager->update_activity(manager, &activity, &ncb_data, callback_activity_manager);
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl
 * Method:     native_1clearActivity
 * Signature:  (Ljava/lang/Object;Lkotlin/jvm/functions/Function2;)V
 */
JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl_native_1clearActivity
  (JNIEnv *env, jobject this_ptr, jobject p_callback)
{
    IDiscordActivityManager* manager;
    GET_INTERFACE_PTR(env, this_ptr, "DiscordActivityManagerImpl", IDiscordActivityManager, manager);

    native_callback_data ncb_data = {.env = env, .callback = p_callback};

    manager->clear_activity(manager, &ncb_data, callback_activity_manager);
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl
 * Method:     native_1sendRequestReply
 * Signature:  (JILjava/lang/Object;Lkotlin/jvm/functions/Function2;)V
 */
JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl_native_1sendRequestReply
  (JNIEnv *env, jobject this_ptr, jlong user_id, jint reply, jobject p_callback)
{
    IDiscordActivityManager* manager;
    GET_INTERFACE_PTR(env, this_ptr, "DiscordActivityManagerImpl", IDiscordActivityManager, manager);

    native_callback_data ncb_data = {.env = env, .callback = p_callback};

    manager->send_request_reply(manager, user_id, (EDiscordActivityJoinRequestReply) reply, &ncb_data, callback_activity_manager);
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl
 * Method:     native_1sendInvite
 * Signature:  (JILjava/lang/String;Ljava/lang/Object;Lkotlin/jvm/functions/Function2;)V
 */
JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl_native_1sendInvite
  (JNIEnv *env, jobject this_ptr, jlong user_id, jint type, jstring content, jobject p_callback)
{
    IDiscordActivityManager* manager;
    GET_INTERFACE_PTR(env, this_ptr, "DiscordActivityManagerImpl", IDiscordActivityManager, manager);

    native_callback_data ncb_data = {.env = env, .callback = p_callback};

    const char* content_native = env->GetStringUTFChars(content, NULL);

    manager->send_invite(manager, user_id, (EDiscordActivityActionType) type, content_native, &ncb_data, callback_activity_manager);

    env->ReleaseStringUTFChars(content, content_native);
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl
 * Method:     native_1acceptInvite
 * Signature:  (JLjava/lang/Object;Lkotlin/jvm/functions/Function2;)V
 */
JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl_native_1acceptInvite
  (JNIEnv *env, jobject this_ptr, jlong user_id, jobject p_callback)
{
    IDiscordActivityManager* manager;
    GET_INTERFACE_PTR(env, this_ptr, "DiscordActivityManagerImpl", IDiscordActivityManager, manager);

    native_callback_data ncb_data = {.env = env, .callback = p_callback};

    manager->accept_invite(manager, user_id, &ncb_data, callback_activity_manager);
}
