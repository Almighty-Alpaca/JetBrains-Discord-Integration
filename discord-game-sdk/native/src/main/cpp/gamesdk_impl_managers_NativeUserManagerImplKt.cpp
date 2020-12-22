#include "gamesdk_impl_managers_NativeUserManagerImplKt.h"

#include "discord_game_sdk.h"
#include "callback.h"
#include "commons.h"
#include "types.h"

#include <functional>

JNIEXPORT jobject JNICALL Java_gamesdk_impl_managers_NativeUserManagerImplKt_getCurrentUser_getCurrentUser(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jUserManager
) {
    auto *userManager = (IDiscordUserManager *) jUserManager;

    DiscordUser user = {};

    enum EDiscordResult result = userManager->get_current_user(userManager, &user);

    return types::createNativeDiscordObjectResult(env, result, types::createJavaUser(*env, user));
}

JNIEXPORT void JNICALL Java_gamesdk_impl_managers_NativeUserManagerImplKt_getCurrentUser_getUser(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jUserManager, jlong jUserId, jobject jCallback
) {
    auto *userManager = (IDiscordUserManager *) jUserManager;

    userManager->get_user(userManager, (DiscordUserId) jUserId, callback::create(env, jCallback), callback::run);
}

JNIEXPORT jobject JNICALL Java_gamesdk_impl_managers_NativeUserManagerImplKt_getCurrentUser_getCurrentUserPremiumType(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jUserManager
) {
    auto *userManager = (IDiscordUserManager *) jUserManager;

    enum EDiscordPremiumType type = {};

    enum EDiscordResult result = userManager->get_current_user_premium_type(userManager, &type);

    return types::createNativeDiscordObjectResult(env, result, types::createIntegerObject(env, type));
}

JNIEXPORT jobject JNICALL Java_gamesdk_impl_managers_NativeUserManagerImplKt_getCurrentUser_currentUserHasFlag(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jUserManager, jint jFlag
) {
    auto *userManager = (IDiscordUserManager *) jUserManager;

    bool value = {};

    enum EDiscordResult result = userManager->current_user_has_flag(userManager, (EDiscordUserFlag) jFlag, &value);

    return types::createNativeDiscordObjectResult(env, result, types::createBooleanObject(env, value));
}
