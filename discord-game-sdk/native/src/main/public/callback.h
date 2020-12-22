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

#ifndef CALLBACK_H
#define CALLBACK_H

#include "commons.h"
#include "discord_game_sdk.h"

#include <jni.h>
#include <functional>

namespace callback {
    void *create(JNIEnv *env, jobject jCallback);

    void run(void *data, EDiscordResult result);

    void run(void *data, EDiscordResult result, const std::function<jobject(JNIEnv &)> &t);

    void run(void *data, EDiscordResult result, DiscordUser *user);
} // namespace callback

#endif // CALLBACK_H
