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

namespace callback
{
    namespace result
    {
        void run(void *data, EDiscordResult result)
        {
            auto *callbackData = (CallbackData *)data;
            jobject jCallbackGlobal = callbackData->jCallback;
            JavaVM *jvm = callbackData->jvm;

            do_with_jnienv(jvm, [callbackData, jCallbackGlobal, result](JNIEnv* env) {
                jclass jCallbackClass = env->GetObjectClass(jCallbackGlobal);
                jmethodID jCallbackMethodInvoke = env->GetMethodID(jCallbackClass, "invoke", "(I)V");

                if (jCallbackMethodInvoke != nullptr) {
                    env->CallObjectMethod(jCallbackGlobal, jCallbackMethodInvoke, (jint) result);
                } else {
                    // TODO: Handle method not found

                    std::cout << "Could not find callback method" << std::endl;
                }

                env->DeleteGlobalRef(jCallbackGlobal);

                delete callbackData;
            });
        }
    } // namespace result
    void *getData(JNIEnv *env, jobject jCallback)
    {
        JavaVM *jvm{};
        env->GetJavaVM(&jvm);

        jobject jCallbackGlobal = env->NewGlobalRef(jCallback);

        return new CallbackData{.jvm = jvm, .jCallback = jCallbackGlobal};
    }
} // namespace callback
