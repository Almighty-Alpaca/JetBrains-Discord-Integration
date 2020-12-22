#ifndef EVENTS_H
#define EVENTS_H

#include <jni.h>
#include <discord_game_sdk.h>

namespace events {
    void *createData(JNIEnv *env, jobject jEvents);

    IDiscordUserEvents *getUserEvents();

    IDiscordRelationshipEvents *getRelationshipEvents();
}

#endif // EVENTS_H
