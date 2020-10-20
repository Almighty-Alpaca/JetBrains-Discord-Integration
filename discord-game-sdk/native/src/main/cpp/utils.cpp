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

std::function<void(discord::Result)> createResultCallback(JNIEnv *env, jobject jCallback)
{
    jobject jCallbackGlobal = env->NewGlobalRef(jCallback);

    JavaVM *jvm{};
    env->GetJavaVM(&jvm);

    return [jCallbackGlobal, jvm](discord::Result result) -> void {
        JNIEnv *env{};

        jint jAttachResult = jvm->AttachCurrentThread((void **)&env, nullptr);
        if (jAttachResult == JNI_OK)
        {
            jclass jCallbackClass = env->GetObjectClass(jCallbackGlobal);
            jmethodID jCallbackMethodInvoke = env->GetMethodID(jCallbackClass, "invoke", "(I)Ljava.lang.Object");

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

            jint jDetachResult = jvm->DetachCurrentThread();
            if (jDetachResult != JNI_OK)
            {
                // TODO: Check and handle error code (jni.h:160)

                std::cout << "Could not detach from VM! Code: " << jAttachResult << std::endl;
            }
        }
        else
        {
                // TODO: Check and handle error code (jni.h:160). What about the global reference?

            std::cout << "Could not attach to VM! Code: " << jAttachResult << std::endl;
        }
    };
}
