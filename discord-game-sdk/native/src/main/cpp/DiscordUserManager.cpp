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

    return types::createNativeDiscordObjectResult<const DiscordUser&>(*env, result, types::createJavaUser, user);
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

    return types::createNativeDiscordObjectResult(*env, result,types::createIntegerObject, (jint) premium_type);
}

JNIEXPORT jobject JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordUserManagerImpl_native_1currentUserHasFlag
        (JNIEnv *env, jobject jThis, jlong jFlag) {
    IDiscordUserManager *manager;
    GET_INTERFACE_PTR(env, jThis, IDiscordUserManager, manager);

    bool has_flag = false;
    EDiscordResult result = manager->current_user_has_flag(manager, (EDiscordUserFlag) jFlag, &has_flag);

    return types::createNativeDiscordObjectResult(*env, result, types::createBooleanObject, (jboolean ) has_flag);
}
