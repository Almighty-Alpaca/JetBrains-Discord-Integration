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
