#include "commons.h"

jobject asJobjectFromInt(JNIEnv* env, jint int_)
{
    jclass int_class = env->FindClass("java/lang/Integer");
    jmethodID constructor = env->GetMethodID(int_class, "<init>", "(I)V");
    jobject jobj = env->NewObject(int_class, constructor, int_);

    return jobj;
}

jobject asJobjectFromLong(JNIEnv* env, jlong long_)
{
    jclass long_class = env->FindClass("java/lang/Long");
    jmethodID constructor = env->GetMethodID(long_class, "<init>", "(J)V");
    jobject jobj = env->NewObject(long_class, constructor, long_);

    return jobj;
}


jobject createPair(JNIEnv* env, jobject first, jobject second)
{
    jclass pair_class = env->FindClass("kotlin/Pair");
    jmethodID constructor = env->GetMethodID(pair_class, "<init>", "(Ljava/lang/Object;Ljava/lang/Object;)V");
    jobject pair = env->NewObject(pair_class, constructor, first, second);

    return pair;
}
