#include "jnihelpers.h"

#include <iostream>

namespace jnihelpers {
    void withEnv(JavaVM *jvm, const std::function<void(JNIEnv &)>& call) {
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

        call(*env);

        // Only detach if thread wasn't previously attached
        if (getEnvResult == JNI_EDETACHED) {
            jint jDetachResult = jvm->DetachCurrentThread();
            if (jDetachResult != JNI_OK) {
                // TODO: Check and handle error code (jni.h:160)

                std::cout << "Could not detach from VM! Code: " << jDetachResult << std::endl;
            }
        }
    }
}
