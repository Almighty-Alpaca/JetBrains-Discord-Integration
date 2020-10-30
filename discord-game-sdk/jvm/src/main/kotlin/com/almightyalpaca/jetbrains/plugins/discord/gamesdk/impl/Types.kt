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

package com.almightyalpaca.jetbrains.plugins.discord.gamesdk.impl

/**
 * Just a visible hint that this is a pointer T* to help readability
 * It is only used to annotate longs.
 */
@Deprecated("Remove all pointers from public api, use typealias in implementation")
@Target(AnnotationTarget.TYPE)
internal annotation class Pointer<T>

/**
 * Just a visible hint that this is a pointer T** to help readability
 * It is only used to annotate longs.
 */
@Deprecated("Remove all pointers from public api, use typealias in implementation")
@Target(AnnotationTarget.TYPE)
internal annotation class DoublePointer<T>

/**
 * Just a visible hint that this is a void* pointer to help readability
 * It is only used to annotate longs.
 */
@Deprecated("Remove all pointers from public api, use typealias in implementation")
@Target(AnnotationTarget.TYPE)
internal annotation class VoidPointer
