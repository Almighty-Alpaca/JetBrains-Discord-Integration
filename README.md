<h1 align="center">JetBrains Discord Integration</h1>
<p align="center">
  <a href="https://plugins.jetbrains.com/plugin/10233-discord-integration">
    <img src="https://img.shields.io/jetbrains/plugin/v/10233-discord-integration.svg?style=flat-square&label=Current+Version&colorA=606060&colorB=3CB110" alt="JetBrains Plugin Repository">
  </a>
  <a href="https://plugins.jetbrains.com/plugin/10233-discord-integration">
    <img src="https://img.shields.io/jetbrains/plugin/d/10233-discord-integration.svg?style=flat-square&label=Downloads&colorA=606060&colorB=3CB110" alt="JetBrains Plugin Repository">
  </a>
  <a href="https://plugins.jetbrains.com/plugin/10233-discord-integration">
    <img src="https://img.shields.io/badge/License-Apache--2.0-FFC133.svg?style=flat-square&colorA=606060" alt="JetBrains Plugin Repository">
  </a>
  <a href="https://discord.gg/SvuyuMP">
    <img src="https://img.shields.io/discord/464395429392678912.svg?logo=discord&style=flat-square&label=Discord&colorA=7289DA&colorB=606060" alt="Discord">
  </a>
</p>

----

## Installation...

### ...from the JetBrains Plugin Repository (this is what you want to do)

Open your IDE, go to Settings > Plugins > Browse repositories... > Search for "Discord Integration" and select this plugin (be sure to select the right one, there a a total of 3 Discord plugins)

#### ...from the GitHub release page
Go to the [GitHub relase page](/releases/latest) and grab the latest `JetBrains-Discord-Integration-X.X.X.zip`. In your IDE go to `Settings > Plugins > Install plugin from disk...` and seleft the previously downloaded zip file.

#### ...or from source

As this project uses Gradle it's very easy to compile yourself. Be aware though that depending on your system this make take a bit as it needs download the sources for IntelliJ first.
Open your favourite terminal and execute the following commands:
```bash
git clone https://github.com/Almighty-Alpaca/JetBrains-Discord-Integration.git
cd JetBrains-Discord-Integration
# The next line is only neccesary on linux to make the gradle wrapper executable
chmod +x gradlew
./gradlew build
```
The plugin zip will be generated under `build/distributions/JetBrains-Discord-Integration-X.X.X.zip`.

To install the zip file follow the steps from the [previous install method](#from-the-github-release-page).

## Support...

### ...by joining the plugin Discord server

There is a [dedicated Discord server](https://discord.gg/SvuyuMP) for the JetBrains Discord Integration Plugin where we provide support for the plugin and have many of the users of the plugin chilling out.

[![JetBrains Discord Integration Plugin Server](https://discordapp.com/api/guilds/464395429392678912/embed.png?style=banner3)](https://discord.gg/SvuyuMP)

### ...using GitHub issues

For feature requests and bug reports, please make use of the GitHub issues by submitting a new issue. Please check whether someone has reported your issue already before creating your own report. To request new icons go to the [JetBrains Discord Integration Icons](https://github.com/Almighty-Alpaca/JetBrains-Discord-Integration-Icons) repository and open an issue there instead. Please include as much detail as possible in your issue and how to reproduce it if possible and relevant.

#### ...or why not join the JetBrains community server

If you have questions regarding any JetBrains IDE or other JetBrains products and projects feel free to join the [JetBrains community Discord server](https://discord.gg/9ut9sqD) where many users of JetBrains hang out alongside some of the JetBrains team.

[![JetBrains Community Discord Server](https://discordapp.com/api/guilds/433980600391696384/embed.png?style=banner2)](https://discord.gg/9ut9sqD)


## License

This project has released under the Apache 2.0 license. You can see the full license [here](/LICENSE.md)
