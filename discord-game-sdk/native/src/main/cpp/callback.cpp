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

#include "commons.h"
#include "jnihelpers.h"
#include "types.h"

namespace callback {
    struct CallbackData {
        JavaVM &jvm;
        jobject jCallback;
    };

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
            jclass jCallbackClass = env.FindClass("kotlin/jvm/functions/Function1");
            jmethodID jCallbackMethodInvoke = env.GetMethodID(jCallbackClass, "invoke", "(Ljava/lang/Object;)Ljava/lang/Object;");

            if (jCallbackMethodInvoke != nullptr) {
                env.CallObjectMethod(jCallbackGlobal, jCallbackMethodInvoke, types::createIntegerObject(env, (jint) result));
            } else {
                // TODO: Handle method not found

                std::cout << "Could not find callback method" << std::endl;
            }

            env.DeleteGlobalRef(jCallbackGlobal);
        });

        delete callbackData;
    }

    void run(void *data, EDiscordResult result, const std::function<jobject(JNIEnv &)> &t) {
        auto *callbackData = (CallbackData *) data;

        jobject jCallbackGlobal = callbackData->jCallback;
        JavaVM &jvm = callbackData->jvm;

        jnihelpers::withEnv(jvm, [& jCallbackGlobal, & result, & t](JNIEnv &env) {
            jclass jCallbackClass = env.GetObjectClass(jCallbackGlobal);
            jmethodID jCallbackMethodInvoke = env.GetMethodID(jCallbackClass, "invoke", "(I)V");

            if (jCallbackMethodInvoke != nullptr) {
                jobject jResult = types::createNativeDiscordObjectResult(env, result, t(env));

                env.CallObjectMethod(jCallbackGlobal, jCallbackMethodInvoke, jResult);
            } else {
                // TODO: Handle method not found

                std::cout << "Could not find callback method" << std::endl;
            }

            env.DeleteGlobalRef(jCallbackGlobal);
        });

        delete callbackData;
    }

    void run(void *data, EDiscordResult result, DiscordUser *user) {
        std::function<jobject(JNIEnv &)> converter = [&user](JNIEnv &env) -> jobject {
            return types::createJavaUser(env, *user);
        };

        run(data, result, converter);
    }
} // namespace callback
