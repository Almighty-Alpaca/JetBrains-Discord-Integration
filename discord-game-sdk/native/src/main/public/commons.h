#ifndef _INCLUDED_DEFINITIONS
#define _INCLUDED_DEFINITIONS

#define INTERNAL_THIS_POINTER_NAME "internalThisPointer"
#define CLASSNAME(name) ("Lcom/almightyalpaca/jetbrains/plugins/discord/gamesdk/" name ";")

#define GET_INTERFACE_PTR(__jni, __this, classname, type) (([](JNIEnv * __jni_env, jobject __this_ptr) -> type* {   \
    jclass __clazz;                                                                                                 \
    jfieldID __ptr_field_id;                                                                                        \
                                                                                                                    \
    __clazz = __jni_env->GetObjectClass(__this_ptr);                                                                \
    __ptr_field_id = __jni_env->GetFieldID(__clazz, INTERNAL_THIS_POINTER_NAME, CLASSNAME(classname));              \
    jlong __ptr = __jni_env->GetLongField(__this_ptr, __ptr_field_id);                                              \
    return (type*) __ptr;                                                                                           \
})(__jni, __this))

#endif
