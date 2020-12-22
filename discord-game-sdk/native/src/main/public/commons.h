#ifndef COMMONS_H
#define COMMONS_H

#include "discord_game_sdk.h"

#include <jni.h>

#define INTERNAL_THIS_POINTER_NAME "internalThisPointer"
#define CLASSNAME(name) ("Lcom/almightyalpaca/jetbrains/plugins/discord/gamesdk/" name ";")

#define GET_INTERFACE_PTR(jni, this_, type, var) {                                                                  \
    jclass clazz;                                                                                                   \
    jfieldID ptr_field_id;                                                                                          \
                                                                                                                    \
    clazz = (jni)->GetObjectClass(this_);                                                                             \
    ptr_field_id = (jni)->GetFieldID(clazz, INTERNAL_THIS_POINTER_NAME, "J");                                         \
    jlong ptr = (jni)->GetLongField(this_, ptr_field_id);                                                             \
    (var) = (type*) ptr;                                                                                              \
}

jobject createIntegerObject(JNIEnv *env, jint value);

jobject createLongObject(JNIEnv *env, jlong value);

jobject createBooleanObject(JNIEnv *env, jboolean value);

jobject createPair(JNIEnv *env, jobject first, jobject second);

jobject createNativeDiscordObjectResult(JNIEnv *env, enum EDiscordResult result, jobject object);

jobject createJavaDiscordUser(JNIEnv *env, DiscordUser *user);

#endif // COMMONS_H
