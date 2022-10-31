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

import org.apache.commons.lang3.StringUtils
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

operator fun ExtraPropertiesExtension.contains(key: String) = has(key)

operator fun <T> Property<T>.invoke(value: T): Unit = this.set(value)
operator fun <T> Property<T>.invoke(value: Provider<T>): Unit = this.set(value)

operator fun <T> ListProperty<T>.invoke(value: T, vararg values: T): Unit = this.addAll(value, *values)
operator fun <T> ListProperty<T>.invoke(values: Iterable<T>): Unit = this.addAll(values)
operator fun <T> ListProperty<T>.invoke(values: Provider<Iterable<T>>): Unit = this.addAll(values)

operator fun <T> Provider<T>.invoke(): T = this.get()

fun kotlinLanguageVersion(dependencyVersion: String): String {
    return when (val index = StringUtils.ordinalIndexOf(dependencyVersion, ".", 2)) {
        -1 -> dependencyVersion
        else -> dependencyVersion.substring(0, index)
    }
}
