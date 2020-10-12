
#include "greeter.h"
#include "discord_game_sdk.h"

#include <string>

std::string say_hello(std::string name) {
	return "Bonjour, " + name + "! I'm using Discord GameSDK version " + std::to_string(DISCORD_VERSION) + "!";
}
