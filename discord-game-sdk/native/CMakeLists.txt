#[[
# Copyright 2017-2020 Aljoscha Grebe
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#   https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#]]

cmake_minimum_required(VERSION 3.1...3.19)

project(native)

file(GLOB SOURCE_FILES CONFIGURE_DEPENDS "src/main/cpp/*.cpp")

add_library(native SHARED ${SOURCE_FILES})

add_executable(test ${SOURCE_FILES})

target_compile_features(native PUBLIC cxx_std_17)
target_compile_features(test PUBLIC cxx_std_17)

if (MSVC)
    target_compile_definitions(native PUBLIC NOMINMAX)
    target_compile_definitions(test PUBLIC NOMINMAX)
endif ()

target_include_directories(native PUBLIC "../jvm/build/generated/headers/" "src/main/headers/" "src/main/public/")
target_include_directories(test PUBLIC "../jvm/build/generated/headers/" "src/main/headers/" "src/main/public/")

find_package(JNI REQUIRED)

target_include_directories(native PUBLIC ${JNI_INCLUDE_DIRS})
target_include_directories(test PUBLIC ${JNI_INCLUDE_DIRS})

if (${CMAKE_SIZEOF_VOID_P} STREQUAL "8")
    set(ARCHITECTURE_TYPE x86-64) # 64 Bit
else ()
    set(ARCHITECTURE_TYPE x86) # 32 Bit
endif ()

find_library(
        DISCORD_LIBRARY
        NAMES "${CMAKE_SHARED_LIBRARY_PREFIX}discord_game_sdk${CMAKE_SHARED_LIBRARY_SUFFIX}"
        NO_CMAKE_FIND_ROOT_PATH
        HINTS "lib/discord_game_sdk/"
        PATH_SUFFIXES "windows/${ARCHITECTURE_TYPE}" "linux/${ARCHITECTURE_TYPE}" "macos/${ARCHITECTURE_TYPE}"
)

target_link_libraries(native PUBLIC ${DISCORD_LIBRARY})
target_link_libraries(test PUBLIC ${DISCORD_LIBRARY})

if (MSVC)
    string(REPLACE ".lib" "" DISCORD_RUNTIME_LIBRARY ${DISCORD_LIBRARY})
else ()
    set(DISCORD_RUNTIME_LIBRARY ${DISCORD_LIBRARY})
endif ()

add_custom_target(DLLS COMMAND ${CMAKE_COMMAND} -E copy ${DISCORD_RUNTIME_LIBRARY} ${CMAKE_BINARY_DIR})

add_dependencies(native DLLS)
add_dependencies(test DLLS)
