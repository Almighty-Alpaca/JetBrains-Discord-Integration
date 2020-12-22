#ifndef Included_callback_typed
#define Included_callback_typed

#include "discord_game_sdk.h"
#include "callback.h"

namespace callback {
    namespace typed {
        template<typename T>
        void run(void* data, EDiscordResult result, T&& f) {
            auto *callbackData = (CallbackData *)data;
            jobject jCallbackGlobal = callbackData->jCallback;
            JavaVM *jvm = callbackData->jvm;

            do_with_jnienv(jvm, [callbackData, jCallbackGlobal, result, f](JNIEnv* env) {
                jclass jCallbackClass = env->GetObjectClass(jCallbackGlobal);
                jmethodID jCallbackMethodInvoke = env->GetMethodID(jCallbackClass, "invoke", "(ILjava/lang/Object;)V");

                if (jCallbackMethodInvoke != nullptr) {
                    env->CallObjectMethod(jCallbackGlobal, jCallbackMethodInvoke, (jint) result, f(env));
                } else {
                    // TODO: Handle method not found

                    std::cout << "Could not find callback method" << std::endl;
                }

                env->DeleteGlobalRef(jCallbackGlobal);

                delete callbackData;
            });
        }
    }

}

#endif
