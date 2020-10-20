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

#include "utils.h"

#include <iostream>

/*
* TODO: Maybe the can be simplified when I understand the threading model better
* If truly all callbacks are run on the thread that calls discord::Core::RunCallbacks(),
* we could save a great deal of complexity by running it in a permanently attached thread.
*/
std::function<void(discord::Result)> createResultCallback(JNIEnv *env, jobject const jCallback)
{
    jobject jCallbackGlobal = env->NewGlobalRef(jCallback);

    JavaVM *jvm{};
    env->GetJavaVM(&jvm);

    return [jCallbackGlobal, jvm](discord::Result result) -> void {
        JNIEnv *env{};

        jint getEnvResult = jvm->GetEnv((void **)&env, JNI_VERSION_1_8);

        if (getEnvResult == JNI_EVERSION)
        {
            // TODO: handle wrong version
        }
        else if (getEnvResult == JNI_EDETACHED)
        {
            jint jAttachResult = jvm->AttachCurrentThread((void **)&env, nullptr);

            if (jAttachResult != JNI_OK)
            {
                // TODO: Check and handle error code (jni.h:160). What about the global reference?

                std::cout << "Could not attach to VM! Code: " << jAttachResult << std::endl;
            }
        }

        jclass jCallbackClass = env->GetObjectClass(jCallbackGlobal);
        jmethodID jCallbackMethodInvoke = env->GetMethodID(jCallbackClass, "invoke", "(I)V");

        if (jCallbackMethodInvoke != nullptr)
        {
            env->CallObjectMethod(jCallbackGlobal, jCallbackMethodInvoke, (jint)result);
        }
        else
        {
            // TODO: Handle method not found

            std::cout << "Could not find callback method" << std::endl;
        }

        env->DeleteGlobalRef(jCallbackGlobal);

        // Only detach if thread wasn't previously attached
        if (getEnvResult == JNI_EDETACHED)
        {
            jint jDetachResult = jvm->DetachCurrentThread();
            if (jDetachResult != JNI_OK)
            {
                // TODO: Check and handle error code (jni.h:160)

                std::cout << "Could not detach from VM! Code: " << jDetachResult << std::endl;
            }
        }
    };
}
