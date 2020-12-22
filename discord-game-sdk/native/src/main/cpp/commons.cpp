#include "commons.h"

jobject as_jobject_from_int(JNIEnv* env, jint int_)
{
    jclass int_class = env->FindClass("java/lang/Integer");
    jmethodID j_value_of = env->GetStaticMethodID(int_class, "valueOf", "(I)Ljava/lang/Integer;");
    jobject jobj = env->CallStaticObjectMethod(int_class, j_value_of, int_);

    return jobj;
}

jobject as_jobject_from_long(JNIEnv* env, jlong long_)
{
    jclass long_class = env->FindClass("java/lang/Long");
    jmethodID j_value_of = env->GetStaticMethodID(long_class, "valueOf", "(J)Ljava/lang/Long;");
    jobject jobj = env->CallStaticObjectMethod(long_class, j_value_of, long_);

    return jobj;
}


jobject create_pair(JNIEnv* env, jobject first, jobject second)
{
    jclass pair_class = env->FindClass("kotlin/Pair");
    jmethodID constructor = env->GetMethodID(pair_class, "<init>", "(Ljava/lang/Object;Ljava/lang/Object;)V");
    jobject pair = env->NewObject(pair_class, constructor, first, second);

    return pair;
}

