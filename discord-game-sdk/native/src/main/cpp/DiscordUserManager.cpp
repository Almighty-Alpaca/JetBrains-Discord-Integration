#include "com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordUserManagerImpl.h"
#include "jniclasses.h"
#include "commons.h"
#include "types.h"
#include "callback.h"

JNIEXPORT jobject JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordUserManagerImpl_native_1getCurrentUser
        (JNIEnv *env, jobject jThis) {
    IDiscordUserManager *manager;
    GET_INTERFACE_PTR(env, jThis, IDiscordUserManager, manager);
    DiscordUser user{};
    EDiscordResult result = manager->get_current_user(manager, &user);


    return types::createNativeDiscordObjectResult(*env, result, [&user](JNIEnv &env) {
        return gamesdk::api::types::DiscordUser::constructor0::invoke(
                env, user.id, env.NewStringUTF(user.username), env.NewStringUTF(user.discriminator), env.NewStringUTF(user.avatar), user.bot
        );
    });
}

JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordUserManagerImpl_native_1getUser
        (JNIEnv *env, jobject jThis, jlong jUserId, jobject jCallback) {
    IDiscordUserManager *manager;
    GET_INTERFACE_PTR(env, jThis, IDiscordUserManager, manager);
    void *cb_data = callback::create(env, jCallback);
    manager->get_user(manager, jUserId, cb_data, callback::run);
}

JNIEXPORT jobject JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordUserManagerImpl_native_1getCurrentUserPremiumType
        (JNIEnv *env, jobject jThis) {
    IDiscordUserManager *manager;
    GET_INTERFACE_PTR(env, jThis, IDiscordUserManager, manager);
    EDiscordPremiumType premium_type;
    EDiscordResult result = manager->get_current_user_premium_type(manager, &premium_type);
    return types::createNativeDiscordObjectResult(*env, result, [&premium_type](JNIEnv &env) { return types::createIntegerObject(env, premium_type); });
}

JNIEXPORT jobject JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordUserManagerImpl_native_1currentUserHasFlag
        (JNIEnv *env, jobject jThis, jlong jFlag) {
    IDiscordUserManager *manager;
    GET_INTERFACE_PTR(env, jThis, IDiscordUserManager, manager);
    bool has_flag = false;
    EDiscordResult result = manager->current_user_has_flag(manager, (EDiscordUserFlag) jFlag, &has_flag);
    return types::createNativeDiscordObjectResult(*env, result, [&has_flag](JNIEnv &env) { return types::createBooleanObject(env, has_flag); });
}
