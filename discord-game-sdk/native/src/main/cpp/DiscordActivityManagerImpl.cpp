#include "com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordActivityManagerImpl.h"

#include <cassert>
#include <iostream>

#include "callback.h"
#include "commons.h"
#include "discord_game_sdk.h"
#include "types.h"

IDiscordActivityEvents       activity_manager_events;

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl
 * Method:     native_1registerCommand
 * Signature:  (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordActivityManagerImpl_native_1registerCommand
  (JNIEnv * env, jobject this_ptr, jstring name)
{
    IDiscordActivityManager* manager;
    GET_INTERFACE_PTR(env, this_ptr, IDiscordActivityManager, manager);
    const char* nameNative = env->GetStringUTFChars(name, nullptr);

    EDiscordResult return_code = manager->register_command(manager, nameNative);

    env->ReleaseStringUTFChars(name, nameNative);

    return (jint)return_code;
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl
 * Method:     native_1registerSteam_0002dWZ4Q5Ns
 * Signature:  (I)I
 */
JNIEXPORT jint JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordActivityManagerImpl_native_1registerSteam_0002dWZ4Q5Ns
  (JNIEnv * env, jobject this_ptr, jint steam_id)
{
    IDiscordActivityManager* manager;
    GET_INTERFACE_PTR(env, this_ptr, IDiscordActivityManager, manager);

    return (jint) manager->register_steam(manager, steam_id);
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl
 * Method:     native_1updateActivity
 * Signature:  (Lcom/almightyalpaca/jetbrains/plugins/discord/gamesdk/DeconstructedDiscordActivity;Ljava/lang/Object;Lkotlin/jvm/functions/Function2;)V
 */
JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordActivityManagerImpl_native_1updateActivity
  (JNIEnv *env, jobject this_ptr, jobject p_activity, jobject p_callback)
{
    IDiscordActivityManager* manager;
    GET_INTERFACE_PTR(env, this_ptr, IDiscordActivityManager, manager);

    DiscordActivity activity = types::createDiscordActivity(*env, p_activity);

    auto ncb_data = callback::create(env, p_callback);

    manager->update_activity(manager, &activity, ncb_data, callback::run);
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl
 * Method:     native_1clearActivity
 * Signature:  (Ljava/lang/Object;Lkotlin/jvm/functions/Function2;)V
 */
JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordActivityManagerImpl_native_1clearActivity
  (JNIEnv *env, jobject this_ptr, jobject p_callback)
{
    IDiscordActivityManager* manager;
    GET_INTERFACE_PTR(env, this_ptr, IDiscordActivityManager, manager);

    auto ncb_data = callback::create(env, p_callback);

    manager->clear_activity(manager, ncb_data, callback::run);
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl
 * Method:     native_1sendRequestReply
 * Signature:  (JILjava/lang/Object;Lkotlin/jvm/functions/Function2;)V
 */
JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordActivityManagerImpl_native_1sendRequestReply
  (JNIEnv *env, jobject this_ptr, jlong user_id, jint reply, jobject p_callback)
{
    IDiscordActivityManager* manager;
    GET_INTERFACE_PTR(env, this_ptr, IDiscordActivityManager, manager);

    auto ncb_data = callback::create(env, p_callback);

    manager->send_request_reply(manager, user_id, (EDiscordActivityJoinRequestReply) reply, ncb_data, callback::run);
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl
 * Method:     native_1sendInvite
 * Signature:  (JILjava/lang/String;Ljava/lang/Object;Lkotlin/jvm/functions/Function2;)V
 */
JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordActivityManagerImpl_native_1sendInvite
  (JNIEnv *env, jobject this_ptr, jlong user_id, jint type, jstring content, jobject p_callback)
{
    IDiscordActivityManager* manager;
    GET_INTERFACE_PTR(env, this_ptr, IDiscordActivityManager, manager);

    auto ncb_data = callback::create(env, p_callback);

    const char* content_native = env->GetStringUTFChars(content, nullptr);

    manager->send_invite(manager, user_id, (EDiscordActivityActionType) type, content_native, ncb_data, callback::run);

    env->ReleaseStringUTFChars(content, content_native);
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordActivityManagerImpl
 * Method:     native_1acceptInvite
 * Signature:  (JLjava/lang/Object;Lkotlin/jvm/functions/Function2;)V
 */
JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordActivityManagerImpl_native_1acceptInvite
  (JNIEnv *env, jobject this_ptr, jlong user_id, jobject p_callback)
{
    IDiscordActivityManager* manager;
    GET_INTERFACE_PTR(env, this_ptr, IDiscordActivityManager, manager);

    auto ncb_data = callback::create(env, p_callback);

    manager->accept_invite(manager, user_id, ncb_data, callback::run);
}
