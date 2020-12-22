#include "events.h"

#include <functional>
#include <tuple>
#include <string>
#include <iostream>

#include "types.h"

namespace events {
    struct EventData {
        JavaVM &jvm;
        jobject &jEvents;
    };

    void *createData(JNIEnv *env, jobject jEvents) {
        JavaVM *jvm{};
        env->GetJavaVM(&jvm);

        jobject jEventsGlobal = env->NewGlobalRef(jEvents);

        return new EventData{*jvm, jEventsGlobal};
    }

    template<class... Args>
    void onEvent(
            void *data,
            const std::string &eventBusName,
            const std::string &eventClassName,
            const std::string &eventClassConstructorSignature,
            std::function<std::tuple<Args...>(JNIEnv &)> eventClassConstructorArgs
    ) {
        std::cout << "eventBusName: " << eventBusName << std::endl;
        std::cout << "eventClassName: " << eventClassName << std::endl;
        std::cout << "eventClassConstructorSignature: " << eventClassConstructorSignature << std::endl;

        auto *eventData = (EventData *) data;

        jobject jEventsGlobal = eventData->jEvents;
        JavaVM &jvm = eventData->jvm;

        JNIEnv *envP{};
        jint getEnvResult = jvm.GetEnv((void **) &envP, JNI_VERSION_1_8);

        if (getEnvResult == JNI_EVERSION) {
            // TODO: handle wrong version
        } else if (getEnvResult == JNI_EDETACHED) {
            jint jAttachResult = jvm.AttachCurrentThread((void **) &envP, nullptr);

            if (jAttachResult != JNI_OK) {
                // TODO: Check and handle error code (jni.h:160). What about the global reference?

                std::cout << "Could not attach to VM! Code: " << (long) jAttachResult << std::endl;
            } else {
                jint getEnvResult2 = jvm.GetEnv((void **) &envP, JNI_VERSION_1_8);
                if (getEnvResult2 != JNI_OK) {
                    std::cout << "Could not get environment! Code: " << (long) getEnvResult2 << std::endl;
                }
            }
        }

        JNIEnv &env = *envP;

//        jclass jEventsClass = envP->GetObjectClass(jEventsGlobal);
        jclass jEventsClass = envP->FindClass("gamesdk/impl/Events");

        auto eventBusGetterName = "get" + eventBusName;
        if (eventBusGetterName.size() > 3) {
            eventBusGetterName[3] = toupper(eventBusGetterName[3]);
        }

        std::cout << "eventBusGetterName: " << eventBusGetterName << std::endl;

        jmethodID jEventsGetEventBus = envP->GetMethodID(jEventsClass, eventBusGetterName.c_str(), "()Lgamesdk/impl/events/NativeNotifiableEventBus;");

        std::cout << __LINE__ << ": " << (jEventsGetEventBus == nullptr ? "null" : "not null") << std::endl;

        if (jEventsGetEventBus != nullptr) {
            auto eventClassFullName = "gamesdk/impl/events/" + eventClassName;
            std::cout << "eventClassFullName: " << eventClassFullName << std::endl;

            jclass jEventClass = envP->FindClass(eventClassFullName.c_str());

            std::cout << __LINE__ << ": " << (jEventClass == nullptr ? "null" : "not null") << std::endl;

            jmethodID jEventConstructor = envP->GetMethodID(jEventsClass, "<init>", eventClassConstructorSignature.c_str());

            std::cout << __LINE__ << ": " << (jEventConstructor == nullptr ? "null" : "not null") << std::endl;

//            std::function < jobject(Args...) > f = [&envP, &jEventClass, & jEventConstructor](Args... args) {
//                return envP->NewObject(jEventClass, jEventConstructor, args...);
//            };
//            jobject jEvent = std::apply(f, eventClassConstructorArgs(env));

            auto t = std::tuple_cat(std::forward_as_tuple(env, jEventClass, jEventConstructor), eventClassConstructorArgs(env));
            jobject jEvent = std::apply(&JNIEnv::NewObject, t);

            std::cout << __LINE__ << ": " << (jEvent == nullptr ? "null" : "not null") << std::endl;

            jobject jEventBus = envP->CallObjectMethod(jEventsGlobal, jEventsGetEventBus);

            std::cout << __LINE__ << ": " << (jEventBus == nullptr ? "null" : "not null") << std::endl;

            jclass jEventBusClass = envP->FindClass("gamesdk/impl/events/NativeNotifiableEventBusImpl");

            std::cout << __LINE__ << ": " << (jEventBusClass == nullptr ? "null" : "not null") << std::endl;

            jmethodID jEventBusNotify = envP->GetMethodID(jEventBusClass, "notify", "(Lgamesdk/impl/events/NativeEvent;)V");

            std::cout << __LINE__ << ": " << (jEventBusNotify == nullptr ? "null" : "not null") << std::endl;

            envP->CallObjectMethod(jEventBus, jEventBusNotify, jEvent);

            std::cout << __LINE__ << std::endl;
        } else {
            // TODO: Handle method not found

            std::cout << "Could not find callback method" << std::endl;
        }

        envP->DeleteGlobalRef(jEventsGlobal);

        // Only detach if thread wasn't previously attached
        if (getEnvResult == JNI_EDETACHED) {
            jint jDetachResult = jvm.DetachCurrentThread();
            if (jDetachResult != JNI_OK) {
                // TODO: Check and handle error code (jni.h:160)

                std::cout << "Could not detach from VM! Code: " << jDetachResult << std::endl;
            }
        }
    }

    void onEvent(void *data, const std::string &eventBusName, const std::string &eventClassName) {
        static std::function<std::tuple<>(JNIEnv &)> args = [](JNIEnv &env) -> std::tuple<> {
            return std::tuple<>();
        };

        onEvent(data, eventBusName, eventClassName, "()V", args);
    }

    void onCurrentUserUpdate(void *data) {
        std::cout << "CURRENT USER UPDATE!" << std::endl;
//        onEvent(data, "currentUserUpdates", "NativeCurrentUserUpdateEvent");
    }

    IDiscordUserEvents *getUserEvents() {
        static IDiscordUserEvents events{
                &onCurrentUserUpdate,
        };

        return &events;
    }

    void onRelationshipRefresh(void *data) {
        std::cout << "RELATIONSHIP REFRESH!" << std::endl;
//        onEvent(data, "relationshipRefreshes", "NativeRelationshipRefreshEvent");
    }

    void onRelationshipUpdate(void *data, DiscordRelationship *relationship) {
        std::cout << "RELATIONSHIP UPDATE!" << std::endl;
//        std::function<std::tuple<jobject>(JNIEnv &)> args = [relationship](JNIEnv &env) -> std::tuple<jobject> {
//            return {types::createJavaRelationship(env, *relationship)};
//        };
//
//        onEvent(data, "relationshipUpdates", "NativeRelationshipUpdateEvent", "(Lgamesdk/impl/types/NativeDiscordRelationship)V", args);
    }

    IDiscordRelationshipEvents *getRelationshipEvents() {
        static IDiscordRelationshipEvents events{
                &onRelationshipRefresh,
                &onRelationshipUpdate,
        };

        return &events;
    }
}
