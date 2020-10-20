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

JNIEXPORT jlong JNICALL Java_gamesdk_impl_NativeCoreImplKt_nativeCreate(JNIEnv *, jclass, jlong clientId, jint createFlags)
{
    discord::Core *core{};

    discord::Result result = discord::Core::Create(clientId, (std::uint64_t)createFlags, &core);

    // TODO: Check result

    return (jlong)core;
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeCoreImplKt_destroy(JNIEnv *, jclass, jobject, jlong jCore)
{
    delete (discord::Core *)jCore;
}

JNIEXPORT jint JNICALL Java_gamesdk_impl_NativeCoreImplKt_runCallbacks(JNIEnv *, jclass, jobject, jlong jCore)
{
    discord::Core *core = (discord::Core *)jCore;

    return (jint)core->RunCallbacks();
}
