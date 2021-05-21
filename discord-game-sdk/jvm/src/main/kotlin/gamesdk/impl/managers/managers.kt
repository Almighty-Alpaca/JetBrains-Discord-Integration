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

package gamesdk.impl.managers

import gamesdk.api.managers.*
import gamesdk.impl.NativeCoreImpl
import gamesdk.impl.NativeObjectImpl

internal class NativeApplicationManagerImpl(core: NativeCoreImpl) : NativeObjectImpl.Delegate(core), ApplicationManager

internal class NativeLobbyManagerImpl(core: NativeCoreImpl) : NativeObjectImpl.Delegate(core), LobbyManager

internal class NativeNetworkManagerImpl(core: NativeCoreImpl) : NativeObjectImpl.Delegate(core), NetworkManager

internal class NativeOverlayManagerImpl(core: NativeCoreImpl) : NativeObjectImpl.Delegate(core), OverlayManager

internal class NativeStorageManagerImpl(core: NativeCoreImpl) : NativeObjectImpl.Delegate(core), StorageManager

internal class NativeStoreManagerImpl(core: NativeCoreImpl) : NativeObjectImpl.Delegate(core), StoreManager

internal class NativeVoiceManagerImpl(core: NativeCoreImpl) : NativeObjectImpl.Delegate(core), VoiceManager

internal class NativeAchievementManagerImpl(core: NativeCoreImpl) : NativeObjectImpl.Delegate(core), AchievementManager
