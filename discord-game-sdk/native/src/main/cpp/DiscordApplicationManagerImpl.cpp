#include "com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordApplicationManagerImpl.h"

#include "discord_game_sdk.h"
#include "commons.h"
#include "callback.h"
#include "callback_typed.h"

JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordApplicationManagerImpl_native_1validateOrExit
        (JNIEnv *env, jobject this_ptr, jobject callback) {
    IDiscordApplicationManager *manager;
    GET_INTERFACE_PTR(env, this_ptr, IDiscordApplicationManager, manager);

    auto cb_data = callback::getData(env, callback);

    manager->validate_or_exit(manager, cb_data, callback::result::run);
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

static jobject create_oauth2_token(JNIEnv *env, jstring access_token, jstring scopes, jlong expires) {
    jclass oauth2_token_class = env->FindClass("com/almightyalpaca/jetbrains/plugins/discord/gamesdk/api/DiscordOAuth2Token");
    jmethodID constructor = env->GetMethodID(oauth2_token_class, "<init>", "(Ljava.lang.String;Ljava.lang.String;J)V");
    jobject token = env->NewObject(oauth2_token_class, constructor, access_token, scopes, expires);

    return token;
}

static void run_callback_with_discord_oauth2_token(void* data, EDiscordResult result, DiscordOAuth2Token *token) {
    callback::typed::run(data, result, [token](JNIEnv* env) -> jobject {
        jstring j_access_token = env->NewStringUTF(token->access_token);
        jstring j_scopes = env->NewStringUTF(token->scopes);
        jlong j_expires = token->expires;

        jobject j_token = create_oauth2_token(env, j_access_token, j_scopes, j_expires);

        return j_token;
    });
}

JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordApplicationManagerImpl_native_1getOAuth2Token
        (JNIEnv *env, jobject this_ptr, jobject j_callback) {
    IDiscordApplicationManager *manager;
    GET_INTERFACE_PTR(env, this_ptr, IDiscordApplicationManager, manager);

    DiscordOAuth2Token token{};

    auto *cb_data = callback::getData(env, j_callback);

    manager->get_oauth2_token(manager, cb_data, run_callback_with_discord_oauth2_token);
}

JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordApplicationManagerImpl_native_1getTicked
        (JNIEnv *env, jobject this_ptr, jobject callback) {
}
