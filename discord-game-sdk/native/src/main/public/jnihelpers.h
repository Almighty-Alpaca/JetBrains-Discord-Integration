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

#ifndef JNIHELPERS_H
#define JNIHELPERS_H

#include <functional>
#include <iostream>
#include <type_traits>

#include <jni.h>

namespace jnihelpers {
    template<typename T, typename = std::enable_if_t<std::is_invocable_r<void, T, JNIEnv&>::value>>
    void withEnv(JavaVM &jvm, T &&call){
        JNIEnv *env{};
        jint getEnvResult = jvm.GetEnv((void **) &env, JNI_VERSION_1_8);

        if (getEnvResult == JNI_EVERSION) {
            // TODO: handle wrong version
        } else if (getEnvResult == JNI_EDETACHED) {
            jint jAttachResult = jvm.AttachCurrentThread((void **) &env, nullptr);

            if (jAttachResult != JNI_OK) {
                // TODO: Check and handle error code (jni.h:160). What about the global reference?
                std::cout << "Could not attach to VM! Code: " << jAttachResult << std::endl;
            }
        }

        call(*env);

        // Only detach if thread wasn't previously attached
        if (getEnvResult == JNI_EDETACHED) {
            jint jDetachResult = jvm.DetachCurrentThread();
            if (jDetachResult != JNI_OK) {
                // TODO: Check and handle error code (jni.h:160)

                std::cout << "Could not detach from VM! Code: " << jDetachResult << std::endl;
            }
        }
    }
} // namespace jnihelpers

#endif // JNIHELPERS_H
