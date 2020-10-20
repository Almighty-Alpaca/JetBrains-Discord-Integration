/*
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

package gamesdk.impl

import gamesdk.api.Activity
import gamesdk.api.ApplicationId
import gamesdk.impl.utils.CloseableNativeObject
import gamesdk.impl.utils.Native

internal class NativeActivityImpl : CloseableNativeObject(nativeCreate()) {
    var type: Int by nativeProperty(Native::nativeSetType, Native::nativeGetType)
    var applicationId: ApplicationId by nativeProperty(Native::setApplicationId, Native::getApplicationId)
    var name: String by nativeProperty(Native::setName, Native::getName)
    var state: String by nativeProperty(Native::setState, Native::getState)

    override val destructor: Native.(Pointer) -> Unit = Native::destroy
}

internal fun Activity.toNative(): NativeActivityImpl = NativeActivityImpl().apply native@{
    this@native.type = this@toNative.type.toNative()
    this@native.applicationId = applicationId
    this@native.name = name
    this@native.state=state
}

// This one can't have Native as receiver because it's creating the object
private external fun nativeCreate(): Pointer

private external fun Native.destroy(nativeActivity: Pointer)

private external fun Native.nativeSetType(nativeActivity: Pointer, type: Int)

private external fun Native.nativeGetType(nativeActivity: Pointer): Int

private external fun Native.setApplicationId(nativeActivity: Pointer, applicationId: ApplicationId)

private external fun Native.getApplicationId(nativeActivity: Pointer): ApplicationId

private external fun Native.setName(nativeActivity: Pointer, name: String)

private external fun Native.getName(nativeActivity: Pointer): String

private external fun Native.setState(nativeActivity: Pointer, state: String)

private external fun Native.getState(nativeActivity: Pointer): String
