#ifndef _INCLUDED_DEFINITIONS
#define _INCLUDED_DEFINITIONS

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

#endif
