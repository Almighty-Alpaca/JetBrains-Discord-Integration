#include "instance.h"

namespace instance {
    IDiscordCore *getCore(Instance *instance) {
        return instance == nullptr ? nullptr : instance->core;
    }

    IDiscordActivityManager *getActivityManager(Instance *instance) {
        IDiscordCore *core = getCore(instance);
        return core == nullptr ? nullptr : core->get_activity_manager(core);
    }

    IDiscordImageManager *getImageManager(Instance *instance) {
        IDiscordCore *core = getCore(instance);
        return core == nullptr ? nullptr : core->get_image_manager(core);
    }
    IDiscordUserManager *getUserManager(Instance *instance) {
        IDiscordCore *core = getCore(instance);
        return core == nullptr ? nullptr : core->get_user_manager(core);
    }
}
