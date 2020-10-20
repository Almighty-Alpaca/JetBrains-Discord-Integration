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

#include "gamesdk_impl_NativeActivityImplKt.h"

#include "types.h"

JNIEXPORT jlong JNICALL Java_gamesdk_impl_NativeActivityImplKt_nativeCreate(JNIEnv *, jclass)
{
    return (jlong) new discord::Activity{};
}
JNIEXPORT void JNICALL Java_gamesdk_impl_NativeActivityImplKt_destroy(JNIEnv *, jclass, jobject, jlong jActivity)
{
    delete (discord::Activity *)jActivity;
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeActivityImplKt_nativeSetType(JNIEnv *, jclass, jobject, jlong jActivity, jint jType)
{
    discord::Activity *activity = (discord::Activity *)jActivity;

    activity->SetType((discord::ActivityType)jType);
}

JNIEXPORT jint JNICALL Java_gamesdk_impl_NativeActivityImplKt_nativeGetType(JNIEnv *, jclass, jobject, jlong jActivity)
{
    const discord::Activity *activity = (const discord::Activity *)jActivity;

    return (jint)activity->GetType();
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeActivityImplKt_setApplicationId(JNIEnv *, jclass, jobject, jlong jActivity, jlong jApplicationId)
{
    discord::Activity *activity = (discord::Activity *)jActivity;

    activity->SetApplicationId((int64_t)jApplicationId);
}

JNIEXPORT jlong JNICALL Java_gamesdk_impl_NativeActivityImplKt_getApplicationId(JNIEnv *, jclass, jobject, jlong jActivity)
{
    const discord::Activity *activity = (const discord::Activity *)jActivity;

    return (jint)activity->GetApplicationId();
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeActivityImplKt_setName(JNIEnv *env, jclass, jobject, jlong jActivity, jstring jName)
{
    discord::Activity *activity = (discord::Activity *)jActivity;

    const char *name = env->GetStringUTFChars(jName, nullptr);

    activity->SetName(name);

    env->ReleaseStringUTFChars(jName, name);
}

JNIEXPORT jstring JNICALL Java_gamesdk_impl_NativeActivityImplKt_getName(JNIEnv *env, jclass, jobject, jlong jActivity)
{
    const discord::Activity *activity = (const discord::Activity *)jActivity;

    const char *name = activity->GetName();

    return env->NewStringUTF(name);
}

JNIEXPORT void JNICALL Java_gamesdk_impl_NativeActivityImplKt_setState(JNIEnv *env, jclass, jobject, jlong jActivity, jstring jState)
{
    discord::Activity *activity = (discord::Activity *)jActivity;

    const char *state = env->GetStringUTFChars(jState, nullptr);

    activity->SetState(state);

    env->ReleaseStringUTFChars(jState, state);
}

JNIEXPORT jstring JNICALL Java_gamesdk_impl_NativeActivityImplKt_getState(JNIEnv *env, jclass, jobject, jlong jActivity)
{
    const discord::Activity *activity = (const discord::Activity *)jActivity;

    const char *state = activity->GetState();

    return env->NewStringUTF(state);
}
