/**
 * Copyright 2017-2020 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "events.h"

#include <functional>
#include <iostream>
#include <string>
#include <tuple>

#include "jnihelpers.h"
#include "types.h"

namespace events {
    struct EventData {
        JavaVM &jvm;
        jobject jEventsGlobal;
    };

    void *create(JNIEnv &env, jobject jEvents) {
        JavaVM *jvm{};
        jint result = env.GetJavaVM(&jvm);
        // TODO: handle result

        jobject jEventsGlobal = env.NewGlobalRef(jEvents);

        return new EventData{*jvm, jEventsGlobal};
    }

    void remove(void *data) {
        auto *eventData = (EventData *) data;

        JavaVM &jvm = eventData->jvm;
        jobject jEventsGlobal = eventData->jEventsGlobal;

        jnihelpers::withEnv(jvm, [& jEventsGlobal](JNIEnv &env) {
            env.DeleteGlobalRef(jEventsGlobal);
        });

        delete eventData;
    }

    template<class... Args>
    void onEvent(
            void *data,
            const std::string &eventBusName,
            const std::string &eventClassName,
            const std::string &eventClassConstructorSignature,
            std::function<std::tuple<Args...>(JNIEnv &)> eventClassConstructorArgSupplier
    ) {
        auto *eventData = (EventData *) data;

        JavaVM &jvm = eventData->jvm;
        jobject jEventsGlobal = eventData->jEventsGlobal;

        jnihelpers::withEnv(jvm, [& jEventsGlobal, & eventBusName, & eventClassName, &eventClassConstructorSignature, &eventClassConstructorArgSupplier](JNIEnv &env) {
            jclass jEventsClass = env.GetObjectClass(jEventsGlobal);

            auto eventBusGetterName = "get" + eventBusName;
            if (eventBusGetterName.size() > 3) {
                eventBusGetterName[3] = toupper(eventBusGetterName[3]);
            }

            jmethodID jEventsGetEventBus = env.GetMethodID(jEventsClass, eventBusGetterName.c_str(), "()Lgamesdk/impl/events/NativeNotifiableEventBus;");

            auto eventClassFullName = "gamesdk/impl/events/" + eventClassName;

            jclass jEventClass = env.FindClass(eventClassFullName.c_str());

            jmethodID jEventConstructor = env.GetMethodID(jEventClass, "<init>", eventClassConstructorSignature.c_str());

            std::tuple<Args...> eventClassConstructorArgs = eventClassConstructorArgSupplier(env);

            jobject jEvent = std::apply(&JNIEnv::NewObject, std::tuple_cat(std::forward_as_tuple(env, jEventClass, jEventConstructor), eventClassConstructorArgs));

            jobject jEventBus = env.CallObjectMethod(jEventsGlobal, jEventsGetEventBus);

            jclass jEventBusClass = env.GetObjectClass(jEventBus);

            jmethodID jEventBusNotify = env.GetMethodID(jEventBusClass, "notify", "(Lgamesdk/impl/events/NativeEvent;)V");

            env.CallObjectMethod(jEventBus, jEventBusNotify, jEvent);
        });
    }

    void onEvent(void *data, const std::string &eventBusName, const std::string &eventClassName) {
        static std::function<std::tuple<>(JNIEnv &)> args = [](JNIEnv &env) -> std::tuple<> {
            return std::tuple<>();
        };

        onEvent(data, eventBusName, eventClassName, "()V", args);
    }

    void onCurrentUserUpdate(void *data) {
        onEvent(data, "currentUserUpdates", "NativeCurrentUserUpdateEvent");
    }

    IDiscordUserEvents *getUserEvents() {
        static IDiscordUserEvents events{
                &onCurrentUserUpdate,
        };

        return &events;
    }

    void onRelationshipRefresh(void *data) {
        onEvent(data, "relationshipRefreshes", "NativeRelationshipRefreshEvent");
    }

    void onRelationshipUpdate(void *data, DiscordRelationship *relationship) {
        std::function<std::tuple<jobject>(JNIEnv &)> args = [relationship](JNIEnv &env) -> std::tuple<jobject> {
            return {types::createJavaRelationship(env, *relationship)};
        };

        onEvent(data, "relationshipUpdates", "NativeRelationshipUpdateEvent", "(Lgamesdk/impl/types/NativeDiscordRelationship;)V", args);
    }

    IDiscordRelationshipEvents *getRelationshipEvents() {
        static IDiscordRelationshipEvents events{
                &onRelationshipRefresh,
                &onRelationshipUpdate,
        };

        return &events;
    }
}
