#include "com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordCoreImpl.h"

#include <iostream>

#include "commons.h"
#include "discord_game_sdk.h"
#include "types.h"

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1destroy
 * Signature:  ()V
 */
JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordCoreImpl_native_1destroy
        (JNIEnv *env, jobject this_ptr) {
    IDiscordCore *core;
    GET_INTERFACE_PTR(env, this_ptr, IDiscordCore, core);
    core->destroy(core);
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1runCallbacks
 * Signature:  ()I
 */
JNIEXPORT jint JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordCoreImpl_native_1runCallbacks
        (JNIEnv *env, jobject this_ptr) {
    IDiscordCore *core;
    GET_INTERFACE_PTR(env, this_ptr, IDiscordCore, core);
    return (jint) core->run_callbacks(core);
}

namespace discordcore {
    struct native_callback_data {
        JavaVM *jvm;
        jobject callback;
    };

    static void log_hook_callback(void *vp_cb_data, enum EDiscordLogLevel level, const char *message) {
        auto *cb_data = (native_callback_data *) vp_cb_data;
        jobject j_callback_global = cb_data->callback;
        JavaVM *jvm = cb_data->jvm;
        JNIEnv *env{};

        jint getEnvResult = jvm->GetEnv((void **) &env, JNI_VERSION_1_8);

        if (getEnvResult == JNI_EVERSION) {
            // TODO: handle wrong version
        } else if (getEnvResult == JNI_EDETACHED) {
            jint jAttachResult = jvm->AttachCurrentThread((void **) &env, nullptr);

            if (jAttachResult != JNI_OK) {
                // TODO: Check and handle error code (jni.h:160). What about the global reference?

                std::cout << "Could not attach to VM! Code: " << jAttachResult << std::endl;
            }
        }

        jclass jCallbackClass = env->GetObjectClass(j_callback_global);
        jmethodID jCallbackMethodInvoke = env->GetMethodID(jCallbackClass, "invoke", "(ILjava/lang/String;)V");

        if (jCallbackMethodInvoke != nullptr) {
            jstring j_message = env->NewStringUTF(message);
            env->CallObjectMethod(j_callback_global, jCallbackMethodInvoke, (jint) level, j_message);
        } else {
            // TODO: Handle method not found

            std::cout << "Could not find callback method" << std::endl;
        }

        env->DeleteGlobalRef(j_callback_global);

        // Only detach if thread wasn't previously attached
        if (getEnvResult == JNI_EDETACHED) {
            jint jDetachResult = jvm->DetachCurrentThread();
            if (jDetachResult != JNI_OK) {
                // TODO: Check and handle error code (jni.h:160)

                std::cout << "Could not detach from VM! Code: " << jDetachResult << std::endl;
            }
        }
    }

    static native_callback_data *setup_native_callback_data(JNIEnv *env, jobject j_callback) {
        jobject jCallbackGlobal = env->NewGlobalRef(j_callback);

        JavaVM *jvm{};
        env->GetJavaVM(&jvm);

        auto cb_data = new native_callback_data(); // TODO: Clean up this allocation on `Core.destroy()`
        cb_data->jvm = jvm;
        cb_data->callback = jCallbackGlobal;

        return cb_data;
    }
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1setLogHook
 * Signature:  (ILkotlin/jvm/functions/Function2;)V
 */
JNIEXPORT void JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordCoreImpl_native_1setLogHook
        (JNIEnv *env, jobject this_ptr, jint min_level, jobject p_callback) {
    IDiscordCore *core;
    GET_INTERFACE_PTR(env, this_ptr, IDiscordCore, core);
    auto ncb_data = discordcore::setup_native_callback_data(env, p_callback);

    core->set_log_hook(core, (EDiscordLogLevel) min_level, ncb_data, discordcore::log_hook_callback);
}

#define GetMgr(MANAGER_NAME) {                                                                      \
    IDiscordCore* core;                                                                             \
    GET_INTERFACE_PTR(env, this_ptr, IDiscordCore, core);                                           \
    return (jlong) core->get_ ## MANAGER_NAME ## _manager(core);                                    \
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getApplicationManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordCoreImpl_native_1getApplicationManager
        (JNIEnv *env, jobject this_ptr) {
    GetMgr(application)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getUserManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordCoreImpl_native_1getUserManager
        (JNIEnv *env, jobject this_ptr) {
    GetMgr(user)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getImageManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordCoreImpl_native_1getImageManager
        (JNIEnv *env, jobject this_ptr) {
    GetMgr(image)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getActivityManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordCoreImpl_native_1getActivityManager
        (JNIEnv *env, jobject this_ptr) {
    GetMgr(activity)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getRelationshipManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordCoreImpl_native_1getRelationshipManager
        (JNIEnv *env, jobject this_ptr) {
    GetMgr(relationship)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getLobbyManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordCoreImpl_native_1getLobbyManager
        (JNIEnv *env, jobject this_ptr) {
    GetMgr(lobby)
}
/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getNetworkManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordCoreImpl_native_1getNetworkManager
        (JNIEnv *env, jobject this_ptr) {
    GetMgr(network)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getOverlayManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordCoreImpl_native_1getOverlayManager
        (JNIEnv *env, jobject this_ptr) {
    GetMgr(overlay)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getStorageManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordCoreImpl_native_1getStorageManager
        (JNIEnv *env, jobject this_ptr) {
    GetMgr(storage)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getStoreManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordCoreImpl_native_1getStoreManager
        (JNIEnv *env, jobject this_ptr) {
    GetMgr(store)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getVoiceManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordCoreImpl_native_1getVoiceManager
        (JNIEnv *env, jobject this_ptr) {
    GetMgr(voice)
}

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1getAchievementManager
 * Signature:  ()J
 */
JNIEXPORT jlong JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordCoreImpl_native_1getAchievementManager
        (JNIEnv *env, jobject this_ptr) {
    GetMgr(achievement)
}


/*extern*/ IDiscordUserEvents user_manager_events;
extern IDiscordActivityEvents activity_manager_events;
/*extern*/ IDiscordRelationshipEvents relationship_manager_events;
/*extern*/ IDiscordLobbyEvents lobby_manager_events;
/*extern*/ IDiscordNetworkEvents network_manager_events;
/*extern*/ IDiscordOverlayEvents overlay_manager_events;
/*extern*/ IDiscordStoreEvents store_manager_events;
/*extern*/ IDiscordVoiceEvents voice_manager_events;
/*extern*/ IDiscordAchievementEvents achievement_manager_events;

/*
 * Class:      com_almightyalpaca_jetbrains_plugins_discord_gamesdk_DiscordCoreImpl
 * Method:     native_1create_0002dJSWoG40
 * Signature:  (JI)J
 */
JNIEXPORT jobject JNICALL Java_com_almightyalpaca_jetbrains_plugins_discord_gamesdk_impl_DiscordCoreImpl_native_1create_0002dJSWoG40
        (JNIEnv *env, jclass class_, jlong client_id, jint flags) {
    IDiscordCore *core = nullptr;
    DiscordCreateParams params{};
    DiscordCreateParamsSetDefault(&params);
    params.client_id = client_id;
    params.flags = flags;
    params.events = nullptr;

    params.user_events = &user_manager_events;
    params.activity_events = &activity_manager_events;
    params.relationship_events = &relationship_manager_events;
    params.lobby_events = &lobby_manager_events;
    params.network_events = &network_manager_events;
    params.overlay_events = &overlay_manager_events;
    params.store_events = &store_manager_events;
    params.voice_events = &voice_manager_events;
    params.achievement_events = &achievement_manager_events;

    auto result = DiscordCreate(DISCORD_VERSION, &params, &core);

    return types::createNativeDiscordObjectResult(*env, result, types::createLongObject, (jlong) core);
}
