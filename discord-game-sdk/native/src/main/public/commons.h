#ifndef COMMONS_H
#define COMMONS_H

#include <jni.h>

#include "discord_game_sdk.h"

#define INTERNAL_THIS_POINTER_NAME "internalThisPointer"

#define GET_INTERFACE_PTR(jni, this_, type, var) {                                                                  \
    jclass clazz;                                                                                                   \
    jfieldID ptr_field_id;                                                                                          \
                                                                                                                    \
    clazz = (jni)->GetObjectClass(this_);                                                                             \
    ptr_field_id = (jni)->GetFieldID(clazz, INTERNAL_THIS_POINTER_NAME, "J");                                         \
    jlong ptr = (jni)->GetLongField(this_, ptr_field_id);                                                             \
    (var) = (type*) ptr;                                                                                              \
}

#endif // COMMONS_H
