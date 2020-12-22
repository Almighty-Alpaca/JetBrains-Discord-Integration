#include "commons.h"

#include "discord_game_sdk.h"

jobject createIntegerObject(JNIEnv *env, jint value) {
    jclass int_class = env->FindClass("java/lang/Integer");
    jmethodID j_value_of = env->GetStaticMethodID(int_class, "valueOf", "(I)Ljava/lang/Integer;");
    return env->CallStaticObjectMethod(int_class, j_value_of, value);
}

jobject createLongObject(JNIEnv *env, jlong value) {
    jclass long_class = env->FindClass("java/lang/Long");
    jmethodID j_value_of = env->GetStaticMethodID(long_class, "valueOf", "(J)Ljava/lang/Long;");
    return env->CallStaticObjectMethod(long_class, j_value_of, value);
}

jobject createBooleanObject(JNIEnv *env, jboolean value) {
    jclass jBooleanClass = env->FindClass("java/lang/Boolean");
    jmethodID jBooleanValueOfMethod = env->GetStaticMethodID(jBooleanClass, "valueOf", "(B)Ljava/lang/Boolean;");
    return env->CallStaticObjectMethod(jBooleanClass, jBooleanValueOfMethod, value);
}

jobject createPair(JNIEnv *env, jobject first, jobject second) {
    jclass pair_class = env->FindClass("kotlin/Pair");
    jmethodID constructor = env->GetMethodID(pair_class, "<init>", "(Ljava/lang/Object;Ljava/lang/Object;)V");
    jobject pair = env->NewObject(pair_class, constructor, first, second);

    return pair;
}

jobject createNativeDiscordObjectResult(JNIEnv *env, EDiscordResult result, jobject object) {
    if (result == DiscordResult_Ok) {
        jclass jSuccessClass = env->FindClass("gamesdk/impl/NativeDiscordObjectResult$Success");
        jmethodID jSuccessConstructor = env->GetMethodID(jSuccessClass, "<init>", "(Ljava/lang/Object;)V");
        return env->NewObject(jSuccessClass, jSuccessConstructor, object);
    } else {
        jclass jFailureClass = env->FindClass("gamesdk/impl/NativeDiscordObjectResult$Failure");
        jmethodID jFailureConstructor = env->GetMethodID(jFailureClass, "<init>", "(I)V");
        return env->NewObject(jFailureClass, jFailureConstructor, (jint) result);
    }
}

jobject createJavaDiscordUser(JNIEnv *env, DiscordUser *user) {
    jclass jDiscordUserClass = env->FindClass("com/almightyalpaca/jetbrains/plugins/discord/gamesdk/api/DiscordUser");
    jmethodID jDiscordUserConstructor = env->GetMethodID(jDiscordUserClass, "<init>", "(JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V");

    jlong jId = (jlong) user->id;
    jstring jUsername = env->NewStringUTF(user->username);
    jstring jDiscriminator = env->NewStringUTF(user->discriminator);
    jstring jAvatar = env->NewStringUTF(user->avatar);
    jboolean jBot = (jboolean) user->bot;

    return env->NewObject(jDiscordUserClass, jDiscordUserConstructor, jId, jUsername, jDiscriminator, jAvatar, jBot);
}
