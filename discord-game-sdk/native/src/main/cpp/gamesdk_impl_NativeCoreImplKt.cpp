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

#include "gamesdk_impl_NativeCoreImplKt.h"
#include "core.h"

JNIEXPORT jobject JNICALL Java_gamesdk_impl_NativeCoreImplKt_create(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jClientId, jint jCreateFlags)
{
    discord::Core *core{};

    discord::Result result = discord::Core::Create((discord::ClientId)jClientId, (std::uint64_t)jCreateFlags, &core);

    if (core == nullptr)
    {
        jclass jIntegerClass = env->FindClass("java/lang/Integer");

        if (jIntegerClass != nullptr)
        {
            jmethodID jIntegerValueOf = env->GetStaticMethodID(jIntegerClass, "valueOf", "(I)Ljava/lang/Integer;");

            if (jIntegerValueOf != nullptr)
            {
                return env->CallStaticObjectMethod(jIntegerClass, jIntegerValueOf, (jint)result);
            }
        }
    }
    else
    {
        jclass jLongClass = env->FindClass("java/lang/Long");

        if (jLongClass != nullptr)
        {
            jmethodID jLongValueOf = env->GetStaticMethodID(jLongClass, "valueOf", "(J)Ljava/lang/Long;");

            if (jLongValueOf != nullptr)
            {
                return env->CallStaticObjectMethod(jLongClass, jLongValueOf, (jlong)core);
            }
        }
    }

    // TODO: something is seriously wrong, throw an exception

    return nullptr;
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeCoreImplKt_destroy(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jCore)
{
    delete (discord::Core *)jCore;
}

JNIEXPORT jint JNICALL Java_gamesdk_impl_NativeCoreImplKt_runCallbacks(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jCore)
{
    discord::Core *core = (discord::Core *)jCore;

    return (jint)core->RunCallbacks();
}

JNIEXPORT jlong JNICALL Java_gamesdk_impl_NativeCoreImplKt_getActivityManager(JNIEnv *env, jclass jClass, jobject jReceiver, jlong jCore)
{
    discord::Core *core = (discord::Core *)jCore;

    return (jlong)&core->ActivityManager();
}
