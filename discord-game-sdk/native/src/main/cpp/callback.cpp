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
#include "commons.h"

#include <iostream>

namespace callback::result {
    struct CallbackData {
        JavaVM *jvm;
        jobject jCallback;
    };

    void *create(JNIEnv *env, jobject jCallback) {
        JavaVM *jvm{};
        env->GetJavaVM(&jvm);

        jobject jCallbackGlobal = env->NewGlobalRef(jCallback);

        return new CallbackData{jvm, jCallbackGlobal};
    }

    void run(void *data, EDiscordResult result) {
        auto *callbackData = (CallbackData *) data;
        jobject jCallbackGlobal = callbackData->jCallback;
        JavaVM *jvm = callbackData->jvm;

        JNIEnv *env{};
        jint getEnvResult = jvm->GetEnv((void **) &env, JNI_VERSION_1_8);

        if (getEnvResult == JNI_EVERSION) {
            // TODO: handle wrong version
        } else if (getEnvResult == JNI_EDETACHED) {
            jint jAttachResult = jvm->AttachCurrentThread((void **) &env, nullptr);

            if (jAttachResult != JNI_OK) {
                // TODO: Check and handle error code (jni.h:160). What about the global reference?

                std::cout << "Could not attach to VM! Code: " << (long) jAttachResult << std::endl;
            }
        }

        jclass jCallbackClass = env->FindClass("kotlin/jvm/functions/Function1");
        jmethodID jCallbackMethodInvoke = env->GetMethodID(jCallbackClass, "invoke", "(Ljava/lang/Object;)Ljava/lang/Object;");

        if (jCallbackMethodInvoke != nullptr) {
            env->CallObjectMethod(jCallbackGlobal, jCallbackMethodInvoke, createIntegerObject(env, (jint) result));
        } else {
            // TODO: Handle method not found

            std::cout << "Could not find callback method" << std::endl;
        }

        env->DeleteGlobalRef(jCallbackGlobal);

        // Only detach if thread wasn't previously attached
        if (getEnvResult == JNI_EDETACHED) {
            jint jDetachResult = jvm->DetachCurrentThread();
            if (jDetachResult != JNI_OK) {
                // TODO: Check and handle error code (jni.h:160)

                std::cout << "Could not detach from VM! Code: " << jDetachResult << std::endl;
            }
        }

        delete callbackData;
    }

    namespace object {
        template<class T>
        struct CallbackData {
            JavaVM *jvm;
            jobject jCallback;
            std::function<jobject(JNIEnv *, T &)> converter;
        };

        void *createUser(JNIEnv *env, jobject jCallback) {
            return create<DiscordUser *>(env, jCallback, [](JNIEnv *env, DiscordUser *user) -> jobject { return createJavaDiscordUser(env, user); });
        }

        template<class T>
        void *create(JNIEnv *env, jobject jCallback, std::function<jobject(JNIEnv *, T &)> converter) {
            JavaVM *jvm{};
            env->GetJavaVM(&jvm);

            jobject jCallbackGlobal = env->NewGlobalRef(jCallback);

            return new object::CallbackData<T>{jvm, jCallbackGlobal, converter};
        }

        void run(void *data, EDiscordResult result, DiscordUser *user) {
            run<DiscordUser *>(data, result, user);
        }

        template<class T>
        void run(void *data, EDiscordResult result, T &t) {
            auto *callbackData = (CallbackData<T> *) data;

            jobject jCallbackGlobal = callbackData->jCallback;
            JavaVM *jvm = callbackData->jvm;

            JNIEnv *env{};
            jint getEnvResult = jvm->GetEnv((void **) &env, JNI_VERSION_1_8);

            if (getEnvResult == JNI_EVERSION) {
                // TODO: handle wrong version
            } else if (getEnvResult == JNI_EDETACHED) {
                jint jAttachResult = jvm->AttachCurrentThread((void **) &env, nullptr);

                if (jAttachResult != JNI_OK) {
                    // TODO: Check and handle error code (jni.h:160). What about the global reference?

                    std::cout << "Could not attach to VM! Code: " << jAttachResult << std::endl;
                }
            }

            jclass jCallbackClass = env->GetObjectClass(jCallbackGlobal);
            jmethodID jCallbackMethodInvoke = env->GetMethodID(jCallbackClass, "invoke", "(I)V");

            if (jCallbackMethodInvoke != nullptr) {
                jobject jResult = createNativeDiscordObjectResult(env, result, callbackData->converter(env, t));

                env->CallObjectMethod(jCallbackGlobal, jCallbackMethodInvoke, jResult);
            } else {
                // TODO: Handle method not found

                std::cout << "Could not find callback method" << std::endl;
            }

            env->DeleteGlobalRef(jCallbackGlobal);

            // Only detach if thread wasn't previously attached
            if (getEnvResult == JNI_EDETACHED) {
                jint jDetachResult = jvm->DetachCurrentThread();
                if (jDetachResult != JNI_OK) {
                    // TODO: Check and handle error code (jni.h:160)

                    std::cout << "Could not detach from VM! Code: " << jDetachResult << std::endl;
                }
            }

            delete callbackData;
        } // namespace object
    }     // namespace object
} // namespace callback
