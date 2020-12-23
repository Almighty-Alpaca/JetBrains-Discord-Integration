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

target_compile_features(native PUBLIC cxx_std_17)

find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS} "../jvm/build/generated/headers/" "src/main/headers/" "src/main/public/")

#find_library(
#        DISCORD_LIBRARY discord_game_sdk
#        NO_CMAKE_FIND_ROOT_PATH
#        PATHS "${PROJECT_SOURCE_DIR}/lib/discord_game_sdk/"
#        PATH_SUFFIXES "windows/x86" "windows/x86-64"
#)
#target_link_libraries(native PUBLIC ${DISCORD_LIBRARY})
