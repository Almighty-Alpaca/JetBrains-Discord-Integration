#ifndef INSTANCE_H
#define INSTANCE_H

#include "discord_game_sdk.h"

struct Instance {
    IDiscordCore *core;
    void *eventData;
};

namespace instance {
    IDiscordCore *getCore(Instance *instance);

    IDiscordActivityManager *getActivityManager(Instance *instance);

    IDiscordImageManager *getImageManager(Instance *instance);

    IDiscordUserManager *getUserManager(Instance *instance);
}

#endif //INSTANCE_H
