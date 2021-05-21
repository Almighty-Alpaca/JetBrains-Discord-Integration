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

#ifndef TYPES_H
#define TYPES_H

#include "discord_game_sdk.h"

#include <jni.h>
#include <string>
#include <type_traits>

namespace types {
    template<int N>
    void createNativeString(JNIEnv &env, jbyteArray bytes, char target[N]) {
        std::fill_n(target, N, 0);

        auto length = env.GetArrayLength(bytes);

        auto *buf = (jbyte *) target;

        env.GetByteArrayRegion(bytes, 0, std::min(N - 1L, length), buf); // N-1 to make sure the last char is \0
    }

    std::string createNativeString(JNIEnv &env, jbyteArray string);

    jbyteArray createJavaString(JNIEnv &env, const char *string);

    jobject createIntegerObject(JNIEnv &env, jint value);

    jobject createLongObject(JNIEnv &env, jlong value);

    jobject createBooleanObject(JNIEnv &env, jboolean value);

    /**
     * Activity is an jobject of type NativeDiscordActivity
     */
    DiscordActivity createDiscordActivity(JNIEnv &env, const jobject &jActivity);

    jobject createJavaActivity(JNIEnv &env, const DiscordActivity &activity);

    jobject createJavaImageDimensions(JNIEnv &env, const DiscordImageDimensions &dimensions);

    DiscordImageHandle createDiscordImageHandle(JNIEnv &env, const jobject &jHandle);

    jobject createJavaImageHandle(JNIEnv &env, const DiscordImageHandle &handle);

    jobject createJavaUser(JNIEnv &env, const DiscordUser &user);

    jobject createJavaPresence(JNIEnv &env, const DiscordPresence &presence);

    jobject createJavaRelationship(JNIEnv &env, const DiscordRelationship &relationship);

    jobject createJavaOAuth2Token(JNIEnv &env, DiscordOAuth2Token &token);

    jobject createNativeDiscordObjectResultSuccess(JNIEnv &env, jobject object);

    jobject createNativeDiscordObjectResultFailure(JNIEnv &env, EDiscordResult result);

    jobject createNativeDiscordObjectResult(JNIEnv &env, enum EDiscordResult result, jobject object);

    template<typename T, typename R, typename = std::enable_if<std::is_base_of<jobject, R>::value>>
    jobject createNativeDiscordObjectResult(JNIEnv &env, enum EDiscordResult result, R (&converter)(JNIEnv &, const T), T argument) {
        if (result == DiscordResult_Ok) {
            return createNativeDiscordObjectResultSuccess(env, converter(env, argument));
        } else {
            return createNativeDiscordObjectResultFailure(env, result);
        }
    }
} // namespace types

#endif // TYPES_H
