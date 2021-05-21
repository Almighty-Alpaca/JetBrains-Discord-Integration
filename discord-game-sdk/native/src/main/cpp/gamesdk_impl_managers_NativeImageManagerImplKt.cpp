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

#include "gamesdk_impl_managers_NativeImageManagerImplKt.h"

#include "callback.h"
#include "discord_game_sdk.h"
#include "instance.h"
#include "types.h"

#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCUnusedGlobalDeclarationInspection"
#pragma ide diagnostic ignored "UnusedParameter"

JNIEXPORT void JNICALL Java_gamesdk_impl_managers_NativeImageManagerImplKt_fetch(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jPointer, jobject jHandle, jboolean jRefresh, jobject jCallback
) {
    IDiscordImageManager *imageManager = instance::getImageManager((Instance *) jPointer);

    DiscordImageHandle handle = types::createDiscordImageHandle(*env, jHandle);

    void *data = callback::create(env, jCallback);

    imageManager->fetch(imageManager, handle, (bool) jRefresh, data, callback::run);
}

JNIEXPORT jobject JNICALL Java_gamesdk_impl_managers_NativeImageManagerImplKt_getDimensions(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jPointer, jobject jHandle
) {
    IDiscordImageManager *imageManager = instance::getImageManager((Instance *) jPointer);

    DiscordImageHandle handle = types::createDiscordImageHandle(*env, jHandle);

    DiscordImageDimensions dimensions{};

    EDiscordResult result = imageManager->get_dimensions(imageManager, handle, &dimensions);

    jobject jDimensions = types::createJavaImageDimensions(*env, dimensions);

    return types::createNativeDiscordObjectResult(*env, result, jDimensions);
}

JNIEXPORT jobject JNICALL Java_gamesdk_impl_managers_NativeImageManagerImplKt_getData_0002dpLftnxE(
        JNIEnv *env, jclass jClass, jobject jReceiver, jlong jPointer, jobject jHandle, jint jDataLength
) {
    IDiscordImageManager *imageManager = instance::getImageManager((Instance *) jPointer);

    DiscordImageHandle handle = types::createDiscordImageHandle(*env, jHandle);

    jbyteArray bytes = env->NewByteArray(jDataLength);

    jbyte *bytesData = env->GetByteArrayElements(bytes, JNI_FALSE);

    EDiscordResult result = imageManager->get_data(imageManager, handle, (uint8_t *) bytesData, jDataLength);

    env->ReleaseByteArrayElements(bytes, bytesData, 0);

    return types::createNativeDiscordObjectResult(*env, result, bytes);
}

#pragma clang diagnostic pop
