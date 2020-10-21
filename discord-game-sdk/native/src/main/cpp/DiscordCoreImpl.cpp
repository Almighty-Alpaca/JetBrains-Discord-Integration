#include "commons.h"
#include "com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl.h"
#include "discord_game_sdk.h"

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1destroy
 * Signature:  ()V
 */
JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl_native_1destroy
  (JNIEnv *env, jobject this_ptr)
{
    IDiscordCore* core;
    GET_INTERFACE_PTR(env, this_ptr, "DiscordCoreImpl", IDiscordCore, core);
    core->destroy(core);
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1runCallbacks
 * Signature:  ()I
 */
JNIEXPORT jint JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl_native_1runCallbacks
  (JNIEnv *env, jobject this_ptr)
{
    IDiscordCore* core;
    GET_INTERFACE_PTR(env, this_ptr, "DiscordCoreImpl", IDiscordCore, core);
    return (jint) core->run_callbacks(core);
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1setLogHook
 * Signature:  (ILjava/lang/Object;Lkotlin/jvm/functions/Function3;)V
 */
JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl_native_1setLogHook
  (JNIEnv * env, jobject this_ptr, jint min_level, jobject hook_data, jobject hook)
{
    IDiscordCore* core;
    GET_INTERFACE_PTR(env, this_ptr, "DiscordCoreImpl", IDiscordCore, core);
    /// TODO: Make global refs here to the hook_data and hook (https://stackoverflow.com/a/35950745/12576629) then make a native callback that calls the hook.
}

#define GetMgr(MANAGER_NAME) {                                                                      \
    IDiscordCore* core;                                                                             \
    GET_INTERFACE_PTR(env, this_ptr, "DiscordCoreImpl", IDiscordCore, core);                        \
    return (jlong) core->get_ ## MANAGER_NAME ## _manager(core);                                    \
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getApplicationManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl_native_1getApplicationManager
  (JNIEnv *env, jobject this_ptr)
{
    GetMgr(application)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getUserManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl_native_1getUserManager
  (JNIEnv *env, jobject this_ptr)
{
    GetMgr(user)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getImageManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl_native_1getImageManager
  (JNIEnv *env, jobject this_ptr)
{
    GetMgr(image)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getActivityManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl_native_1getActivityManager
  (JNIEnv *env, jobject this_ptr)
{
    GetMgr(activity)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getRelationshipManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl_native_1getRelationshipManager
  (JNIEnv *env, jobject this_ptr)
{
    GetMgr(relationship)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getLobbyManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl_native_1getLobbyManager
  (JNIEnv *env, jobject this_ptr)
{
    GetMgr(lobby)
}
/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getNetworkManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl_native_1getNetworkManager
  (JNIEnv *env, jobject this_ptr)
{
    GetMgr(network)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getOverlayManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl_native_1getOverlayManager
  (JNIEnv *env, jobject this_ptr)
{
    GetMgr(overlay)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getStorageManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl_native_1getStorageManager
  (JNIEnv *env, jobject this_ptr)
{
    GetMgr(storage)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getStoreManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl_native_1getStoreManager
  (JNIEnv *env, jobject this_ptr)
{
    GetMgr(store)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getVoiceManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl_native_1getVoiceManager
  (JNIEnv *env, jobject this_ptr)
{
    GetMgr(voice)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getAchievementManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl_native_1getAchievementManager
  (JNIEnv *env, jobject this_ptr)
{
    GetMgr(achievement)
}


/*extern*/ IDiscordUserEvents           user_manager_events;
extern IDiscordActivityEvents       activity_manager_events;
/*extern*/ IDiscordRelationshipEvents   relationship_manager_events;
/*extern*/ IDiscordLobbyEvents          lobby_manager_events;
/*extern*/ IDiscordNetworkEvents        network_manager_events;
/*extern*/ IDiscordOverlayEvents        overlay_manager_events;
/*extern*/ IDiscordStoreEvents          store_manager_events;
/*extern*/ IDiscordVoiceEvents          voice_manager_events;
/*extern*/ IDiscordAchievementEvents    achievement_manager_events;

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1create_0002dJSWoG40
 * Signature:  (JI)J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl_native_1create_0002dJSWoG40
  (JNIEnv *env, jclass __class, jlong client_id, jint flags)
{
    IDiscordCore* core;
    DiscordCreateParams params;
    DiscordCreateParamsSetDefault(&params);
    params.client_id = client_id;
    params.flags = flags;
    params.events = nullptr;

    params.user_events = &user_manager_events;
    params.activity_events = &activity_manager_events;
    params.relationship_events = &relationship_manager_events;
    params.lobby_events = &lobby_manager_events;
    params.network_events = &network_manager_events;
    params.overlay_events = &overlay_manager_events;
    params.store_events = &store_manager_events;
    params.voice_events = &voice_manager_events;
    params.achievement_events = &achievement_manager_events;

    DiscordCreate(DISCORD_VERSION, &params, &core);

    return (jlong) core;
}
