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

#include "gamesdk_impl_managers_NativeUserManagerImplKt.h"

#include "callback.h"
#include "discord_game_sdk.h"
#include "instance.h"
#include "types.h"

#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCUnusedGlobalDeclarationInspection"
#pragma ide diagnostic ignored "UnusedParameter"

JNIEXPORT jobject JNICALL Java_gamesdk_impl_managers_NativeUserManagerImplKt_getCurrentUser(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jPointer
) {
    IDiscordUserManager *userManager = instance::getUserManager((Instance *) jPointer);

    DiscordUser user{};
    EDiscordResult result = userManager->get_current_user(userManager, &user);

    return types::createNativeDiscordObjectResult<const DiscordUser &>(*env, result, types::createJavaUser, user);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_managers_NativeUserManagerImplKt_getUser(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jPointer, jlong jUserId, jobject jCallback
) {
    IDiscordUserManager *userManager = instance::getUserManager((Instance *) jPointer);

    userManager->get_user(userManager, (DiscordUserId) jUserId, callback::create(env, jCallback), callback::run);
}

JNIEXPORT jobject JNICALL Java_gamesdk_impl_managers_NativeUserManagerImplKt_getCurrentUserPremiumType(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jPointer
) {
    IDiscordUserManager *userManager = instance::getUserManager((Instance *) jPointer);

    EDiscordPremiumType type;
    EDiscordResult result = userManager->get_current_user_premium_type(userManager, &type);

    return types::createNativeDiscordObjectResult(*env, result, types::createIntegerObject, (jint) type);
}

JNIEXPORT jobject JNICALL Java_gamesdk_impl_managers_NativeUserManagerImplKt_currentUserHasFlag(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jPointer, jint jFlag
) {
    IDiscordUserManager *userManager = instance::getUserManager((Instance *) jPointer);

    bool value;
    EDiscordResult result = userManager->current_user_has_flag(userManager, (EDiscordUserFlag) jFlag, &value);

    return types::createNativeDiscordObjectResult(*env, result, types::createBooleanObject, (jboolean) value);
}

#pragma clang diagnostic pop
