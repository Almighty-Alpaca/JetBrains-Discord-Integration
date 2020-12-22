#ifndef INCLUDED_COMMONS
#define INCLUDED_COMMONS

#include <jni.h>

#define INTERNAL_THIS_POINTER_NAME "internalThisPointer"

#define GET_INTERFACE_PTR(jni, this_, type, var) {                                                                  \
    jclass clazz;                                                                                                   \
    jfieldID ptr_field_id;                                                                                          \
                                                                                                                    \
    clazz = jni->GetObjectClass(this_);                                                                             \
    ptr_field_id = jni->GetFieldID(clazz, INTERNAL_THIS_POINTER_NAME, "J");                                         \
    jlong ptr = jni->GetLongField(this_, ptr_field_id);                                                             \
    var = (type*) ptr;                                                                                              \
}

jobject as_jobject_from_int(JNIEnv* env, jint int_);
jobject as_jobject_from_long(JNIEnv* env, jlong long_);
jobject create_pair(JNIEnv* env, jobject first, jobject second);

#endif
