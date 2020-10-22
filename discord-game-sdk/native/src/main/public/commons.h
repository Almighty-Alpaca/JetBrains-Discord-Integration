#ifndef _INCLUDED_COMMONS
#define _INCLUDED_COMMONS

#include <jni.h>

#define INTERNAL_THIS_POINTER_NAME "internalThisPointer"
#define CLASSNAME(name) ("Lcom/almightyalpaca/jetbrains/plugins/discord/gamesdk/" name ";")

#define GET_INTERFACE_PTR(jni, this_, type, var) {                                                                  \
    jclass clazz;                                                                                                   \
    jfieldID ptr_field_id;                                                                                          \
                                                                                                                    \
    clazz = jni->GetObjectClass(this_);                                                                             \
    ptr_field_id = jni->GetFieldID(clazz, INTERNAL_THIS_POINTER_NAME, "J");                                         \
    jlong ptr = jni->GetLongField(this_, ptr_field_id);                                                             \
    var = (type*) ptr;                                                                                              \
}

jobject asJobjectFromInt(JNIEnv* env, jint int_);
jobject asJobjectFromLong(JNIEnv* env, jlong long_);
jobject createPair(JNIEnv* env, jobject first, jobject second);

#endif
