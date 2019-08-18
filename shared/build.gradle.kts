/*
 * Copyright 2017-2019 Aljoscha Grebe
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

plugins {
    kotlin("jvm")
}

dependencies {
    compile(kotlin(module = "stdlib-jdk8"))
    compile(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-jdk8", version = "1.2.2")

    compile(group = "commons-io", name = "commons-io", version = "2.6")

    compile(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.9.9.3")
    compile(group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml", version = "2.9.9")
}
