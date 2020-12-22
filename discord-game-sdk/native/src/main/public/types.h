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

namespace types {
    jobject createIntegerObject(JNIEnv &env, jint value);

    jobject createLongObject(JNIEnv &env, jlong value);

    jobject createBooleanObject(JNIEnv &env, jboolean value);

    jobject createPair(JNIEnv &env, jobject first, jobject second);

    /**
     * Activity is an jobject of type NativeDiscordActivity
     */
    DiscordActivity createDiscordActivity(JNIEnv &env, jobject &jActivity);

    jobject createJavaActivity(JNIEnv &env, DiscordActivity &activity);

    jobject createJavaUser(JNIEnv &env, DiscordUser &user);

    jobject createJavaPresence(JNIEnv &env, DiscordPresence &presence);

    jobject createJavaRelationship(JNIEnv &env, DiscordRelationship &relationship);

    jobject createNativeDiscordObjectResult(JNIEnv &env, enum EDiscordResult result, jobject object);
} // namespace types

#endif // TYPES_H
