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

#include "gamesdk_impl_NativeActivityManagerImplKt.h"
#include "core.h"
#include "utils.h"

JNIEXPORT jint JNICALL Java_gamesdk_impl_NativeActivityManagerImplKt_registerCommand(JNIEnv *env, jclass, jobject, jlong jCore, jstring jCommand)
{
    discord::Core *core = (discord::Core *)jCore;

    const char *command = env->GetStringUTFChars(jCommand, nullptr);

    discord::Result result = core->ActivityManager().RegisterCommand(command);

    env->ReleaseStringUTFChars(jCommand, command);

    return (jint)result;
}

JNIEXPORT jint JNICALL Java_gamesdk_impl_NativeActivityManagerImplKt_registerSteam_0002dw0wvITU(JNIEnv *env, jclass, jobject, jlong jCore, jint jSteamId)
{
    discord::Core *core = (discord::Core *)jCore;

    discord::Result result = core->ActivityManager().RegisterSteam((uint32_t)jSteamId);

    return (jint)result;
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeActivityManagerImplKt_updateActivity(JNIEnv *env, jclass, jobject, jlong jCore, jlong jActivity, jobject jCallback)
{
    discord::Core *core = (discord::Core *)jCore;
    discord::Activity *activity = (discord::Activity *)jActivity;

    std::function<void(discord::Result)> callback = createResultCallback(env, jCallback);

    core->ActivityManager().UpdateActivity(*activity, callback);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeActivityManagerImplKt_clearActivity(JNIEnv *env, jclass, jobject, jlong jCore, jobject jCallback)
{
    discord::Core *core = (discord::Core *)jCore;

    std::function<void(discord::Result)> callback = createResultCallback(env, jCallback);

    core->ActivityManager().ClearActivity(callback);
}
