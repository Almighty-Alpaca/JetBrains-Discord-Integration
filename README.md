<p align="center">
  <img src="plugin\src\main\resources\META-INF\pluginIcon.svg" alt="JetBrains Discord Integration" width="200"/>
</p>
<h1 align="center">JetBrains Discord Integration</h1>
<p align="center">
  <a href="https://plugins.jetbrains.com/plugin/10233">
    <img src="https://img.shields.io/jetbrains/plugin/v/10233.svg?style=flat-square&label=Current+Version&colorA=606060&colorB=3CB110" alt="JetBrains Plugin Repository">
  </a>
  <a href="https://plugins.jetbrains.com/plugin/10233">
    <img src="https://img.shields.io/jetbrains/plugin/d/10233.svg?style=flat-square&label=Downloads&colorA=606060&colorB=3CB110" alt="JetBrains Plugin Repository">
  </a>
  <a href="https://github.com/Almighty-Alpaca/JetBrains-Discord-Integration/blob/master/LICENSE.md">
    <img src="https://img.shields.io/badge/License-Apache--2.0-FFC133.svg?style=flat-square&colorA=606060" alt="JetBrains Plugin Repository">
  </a>
  <a href="https://discord.gg/SvuyuMP">
    <img src="https://img.shields.io/discord/464395429392678912.svg?logo=discord&logoColor=FFFFFF&style=flat-square&label=Discord&colorA=606060&colorB=7289DA" alt="Discord">
  </a>
</p>

----

## Installation...

### ...from the JetBrains Plugin Repository (this is what you want to do)

Open your IDE, go to Settings > Plugins > Browse repositories... > Search for "Discord Integration" and select this plugin (be sure to select one with the right name, there a total of 3 Discord plugins)

#### ...from the GitHub release page

Go to the [GitHub release page](https://github.com/Almighty-Alpaca/JetBrains-Discord-Integration/releases/latest) and grab the latest `JetBrains-Discord-Integration-X.X.X.zip`. Do not unzip the file. In your IDE go to `Settings > Plugins > Install plugin from disk...` and select the previously downloaded zip file.

#### ...or compile from source

As this project uses Gradle it's very easy to compile yourself. Be aware though that depending on your system this may take a bit as it needs to download the sources for IntelliJ first.
Open your favorite terminal and execute the following commands:

```bash
git clone https://github.com/Almighty-Alpaca/JetBrains-Discord-Integration.git
cd JetBrains-Discord-Integration
# The next line is only necessary on Linux to make the Gradle Wrapper executable
chmod +x gradlew
./gradlew
```
This will generate the file `JetBrains-Discord-Integration-Plugin-X.X.X.zip`.

To install the zip file follow the steps from the [previous install method](#from-the-github-release-page).

## Support...

### ...by joining the plugin Discord server

There is a [dedicated Discord server](https://discord.gg/SvuyuMP) for the JetBrains Discord Integration Plugin where we provide support for the plugin and have many of the users of the plugin chilling out.

[![JetBrains Discord Integration Plugin Server](https://discordapp.com/api/guilds/464395429392678912/embed.png?style=banner3)](https://discord.gg/SvuyuMP)

### ...using GitHub issues

For feature requests and bug reports, feel free to make use of the GitHub issues by submitting a new issue. Please check whether someone has reported your issue already before creating your own report. To not flood the GitHub issues please ask for help in the Discord server first as many issues can be resolved way easier and quicker over there. If you submit a new Issue please include as much detail as possible in your issue and how to reproduce it if possible and relevant.

#### ...or why not join the JetBrains community server

If you have questions regarding any JetBrains IDE or other JetBrains products and projects feel free to join the [JetBrains community Discord server](https://discord.gg/9ut9sqD) where many users of JetBrains hang out alongside some of the JetBrains team.

[![JetBrains Community Discord Server](https://discordapp.com/api/guilds/433980600391696384/embed.png?style=banner2)](https://discord.gg/9ut9sqD)

## License

This project has been released under the Apache 2.0 license. You can see the full license [here](/LICENSE.md)
