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

#include "callback.h"

#include <iostream>

#include "jniclasses.h"
#include "jnihelpers.h"
#include "types.h"

namespace callback {
    void *create(JNIEnv *env, jobject jCallback) {
        JavaVM *jvm;
        jint result = env->GetJavaVM(&jvm);
        // TODO: handle result

        jobject jCallbackGlobal = env->NewGlobalRef(jCallback);

        return new CallbackData{*jvm, jCallbackGlobal};
    }

    void run(void *data, EDiscordResult result) {
        auto *callbackData = (CallbackData *) data;
        jobject jCallbackGlobal = callbackData->jCallback;
        JavaVM &jvm = callbackData->jvm;

        jnihelpers::withEnv(jvm, [&jCallbackGlobal, &result](JNIEnv &env) {
            namespace JNativeCallback = gamesdk::impl::NativeCallback;

            jobject jResult = types::createIntegerObject(env, (jint) result);

            JNativeCallback::invoke(env, jCallbackGlobal, jResult);

            env.DeleteGlobalRef(jCallbackGlobal);
        });

        delete callbackData;
    }

    template<typename T, typename R, typename = std::enable_if<std::is_base_of<jobject, R>::value>>
    void run(void *data, EDiscordResult result, R (&converter)(JNIEnv &, const T), T argument) {
        auto *callbackData = (CallbackData *) data;

        jobject jCallbackGlobal = callbackData->jCallback;
        JavaVM &jvm = callbackData->jvm;

        jnihelpers::withEnv(jvm, [& jCallbackGlobal, & result, & converter, &argument](JNIEnv &env) {
            namespace JNativeCallback = gamesdk::impl::NativeCallback;

            jobject jResult = types::createNativeDiscordObjectResult<T, R>(env, result, converter, argument);

            JNativeCallback::invoke(env, jCallbackGlobal, jResult);

            env.DeleteGlobalRef(jCallbackGlobal);
        });

        delete callbackData;
    }

    void run(void *data, EDiscordResult result, DiscordImageHandle handle) {
        run<const DiscordImageHandle &>(data, result, types::createJavaImageHandle, handle);
    }

    void run(void *data, EDiscordResult result, DiscordUser *user) {
        static jobject (*converter)(JNIEnv &, DiscordUser *) = [](JNIEnv &env, DiscordUser *user) -> jobject {
            return user == nullptr ? nullptr : types::createJavaUser(env, *user);
        };

        run(data, result, *converter, user);
    }

    void run(void *data, EDiscordResult result, DiscordOAuth2Token *token) {
        static jobject (*converter)(JNIEnv &, DiscordOAuth2Token *) =  [](JNIEnv &env, DiscordOAuth2Token *token) -> jobject {
            return token == nullptr ? nullptr : types::createJavaOAuth2Token(env, *token);
        };

        run(data, result, *converter, token);
    }

    void run(void *data, EDiscordResult result, const char *string) {
        static jbyteArray (*converter)(JNIEnv &, const char *) = [](JNIEnv &env, const char *string) -> jbyteArray {
            return string == nullptr ? nullptr : types::createJavaString(env, string);
        };

        run(data, result, *converter, string);
    }
} // namespace callback
