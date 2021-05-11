#include "com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordApplicationManagerImpl.h"

#include "callback.h"
#include "commons.h"
#include "discord_game_sdk.h"

JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordApplicationManagerImpl_native_1validateOrExit
        (JNIEnv *env, jobject this_ptr, jobject callback) {
    IDiscordApplicationManager *manager;
    GET_INTERFACE_PTR(env, this_ptr, IDiscordApplicationManager, manager);

    auto cb_data = callback::create(env, callback);

    manager->validate_or_exit(manager, cb_data, callback::run);
}

JNIEXPORT jstring JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordApplicationManagerImpl_native_1getCurrentLocale
        (JNIEnv *env, jobject this_ptr) {
    IDiscordApplicationManager *manager;
    GET_INTERFACE_PTR(env, this_ptr, IDiscordApplicationManager, manager);

    DiscordLocale locale;

    manager->get_current_locale(manager, &locale);

    jstring j_locale = env->NewStringUTF(locale);

    return j_locale;
}

JNIEXPORT jstring JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordApplicationManagerImpl_native_1getDiscordBranch
        (JNIEnv *env, jobject this_ptr) {
    IDiscordApplicationManager *manager;
    GET_INTERFACE_PTR(env, this_ptr, IDiscordApplicationManager, manager);

    DiscordBranch branch;
    manager->get_current_branch(manager, &branch);

    jstring j_branch = env->NewStringUTF(branch);

    return j_branch;
}

JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordApplicationManagerImpl_native_1getOAuth2Token
        (JNIEnv *env, jobject this_ptr, jobject j_callback) {
    IDiscordApplicationManager *manager;
    GET_INTERFACE_PTR(env, this_ptr, IDiscordApplicationManager, manager);

    auto *cb_data = callback::create(env, j_callback);

    manager->get_oauth2_token(manager, cb_data, callback::run);
}

JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordApplicationManagerImpl_native_1getTicket
        (JNIEnv *env, jobject this_ptr, jobject j_callback) {
    IDiscordApplicationManager *manager;
    GET_INTERFACE_PTR(env, this_ptr, IDiscordApplicationManager, manager);

    auto *cb_data = callback::create(env, j_callback);

    manager->get_ticket(manager, cb_data, callback::run);
}
