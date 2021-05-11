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

#include <tuple>

#include "jniclasses.h"
#include "jnihelpers.h"
#include "types.h"

namespace events {
    namespace JEvents = gamesdk::impl::Events;

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

    template<class... JArgs, class... Args>
    void onEvent(
            void *data,
            jobject (*eventBusGetter)(JNIEnv &, jobject),
            jobject (*eventClassConstructor)(JNIEnv &, JArgs...),
            std::tuple<JArgs...> (*eventClassConstructorArgumentConverter)(JNIEnv &, Args...),
            std::tuple<Args...> eventClassConstructorArguments
    ) {
        auto *eventData = (EventData *) data;

        JavaVM &jvm = eventData->jvm;
        jobject jEventsGlobal = eventData->jEventsGlobal;

        jnihelpers::withEnv(jvm, [& jEventsGlobal, & eventBusGetter, & eventClassConstructor, &eventClassConstructorArgumentConverter, &eventClassConstructorArguments](JNIEnv &env) {
            std::tuple<JArgs...> eventClassConstructorJArgs = std::apply(eventClassConstructorArgumentConverter, std::tuple_cat(std::forward_as_tuple(env), eventClassConstructorArguments));

            jobject jEvent = std::apply(eventClassConstructor, std::tuple_cat(std::forward_as_tuple(env), eventClassConstructorJArgs));

            jobject jEventBus = eventBusGetter(env, jEventsGlobal);

            namespace JNativeNotifiableEventBus = gamesdk::impl::events::NativeNotifiableEventBus;

            JNativeNotifiableEventBus::notify(env, jEventBus, jEvent);
        });
    }

    void onEvent(
            void *data,
            jobject (*eventBusGetter)(JNIEnv &, jobject),
            jobject (*eventClassConstructor)(JNIEnv &)
    ) {
        static std::tuple<> (*argConverter)(JNIEnv &) = [](JNIEnv &env) -> std::tuple<> {
            return std::tuple<>();
        };

        onEvent(data, eventBusGetter, eventClassConstructor, argConverter, std::tuple<>());
    }

    void onCurrentUserUpdate(void *data) {
        namespace JNativeCurrentUserUpdateEvent = gamesdk::impl::events::NativeCurrentUserUpdateEvent;

        onEvent(data, JEvents::getCurrentUserUpdates, JNativeCurrentUserUpdateEvent::constructor0::invoke);
    }

    IDiscordUserEvents *getUserEvents() {
        static IDiscordUserEvents events{
                &onCurrentUserUpdate,
        };

        return &events;
    }

    void onRelationshipRefresh(void *data) {
        namespace JNativeRelationshipRefreshEvent = gamesdk::impl::events::NativeRelationshipRefreshEvent;

        onEvent(data, JEvents::getRelationshipRefreshes, JNativeRelationshipRefreshEvent::constructor0::invoke);
    }

    void onRelationshipUpdate(void *data, DiscordRelationship *relationship) {
        static std::tuple<jobject> (*argConverter)(JNIEnv &, DiscordRelationship *) = [](JNIEnv &env, DiscordRelationship *relationship) -> std::tuple<jobject> {
            return {types::createJavaRelationship(env, *relationship)};
        };

        namespace JNativeRelationshipUpdateEvent = gamesdk::impl::events::NativeRelationshipUpdateEvent;

        onEvent(data, JEvents::getRelationshipUpdates, JNativeRelationshipUpdateEvent::constructor0::invoke, argConverter, std::tuple<DiscordRelationship *>(relationship));
    }

    IDiscordRelationshipEvents *getRelationshipEvents() {
        static IDiscordRelationshipEvents events{
                &onRelationshipRefresh,
                &onRelationshipUpdate,
        };

        return &events;
    }
}
