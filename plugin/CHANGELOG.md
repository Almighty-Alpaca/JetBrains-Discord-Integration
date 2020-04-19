## v1.2.0
- Support for 2020.1 (#77)
- Completely new way how current presence is calculated
- Move settings to "other" category 
- Fix presence for very short project names/custom strings
- Disable timeout option for now due to architectural changes (will be enabled again later)

## v1.1.1
- Fix error when closing a project (for real this time 🙏🏼). (#63)
- Change required IntelliJ version to 2019.1

## v1.1.0
- Fix error when closing a project. (#63)
- Beta testers now get a special role in the server
- Add IDE names without editions (e.g. 'IntelliJ IDEA' instead of 'IntelliJ IDEA Community')
- Show warning in settings when other Discord rich presence plugins are installed

## v1.0.1
- Fix Reading/editing being swapped
- Fix show project prompt always showing
- Fix show project prompt disappearing too quickly
- Fix compatibility with plugins with non-default file system implementations

## v1.0.0
- Brand new codebase written in Kotlin
- Support for multiple icon themes
- Customize every aspect of the rich presence
- Beautiful preview in settings
- Support for project descriptions
- Whole new language detection with support for a wide range of languages and frameworks 
- Adding more languages is now possible in a matter of seconds without a new plugin update  
- ...And so much more I can't even remember what already existed before and what didn't
 
## v0.9.0
- Added F# (#43)
- Added additional C# file extensions (#43)
- Added setting to force big IDE icon (#27)
- Added setting to hide elapsed time (#37)
- Added additional C++ file extensions (#40)
- Added Slim and CoffeeScript (#34)
- Fixed C files not showing (#42)
- Fixed file not reappearing after removing project description (#31)
- Fixed debug folder being created after when debug logging has been disabled
- Updated Java-DiscordRPC to v2.0.1
- Updated JGroups to 4.0.12

## v0.8.0
- Added the ability to add project descriptions (#24)
- Added Dart (#30)
- Added option to hide files (#23)
- Added Twig (#26)
- Added Sass (#25)
- Added PowerShell (#29)
- Updated JGroups to 4.0.11
- Updated Java-DiscordRPC to v1.3.6

## v0.7.2
- Fixed ghost files

## v0.7.1
- Fixed wrong artifact

## v0.7.0
- Added Ceylon (#20)
- Clearing the presence should now work better
- Added experimental and debug settings
  - Added option to enable debug logs
  - Added option to enable the experimental VisibleAreaListener

## v0.6.0
- Added an option to reset the open time after an inactivity timeout
- Added Handlebars.js (#16)
- Added Shell (#18)
- Added Elixir (#17)
- Added Erlang
- Added option hide the presence after a period of inactivity (#10)
- Fixed switching between multiple projects (#12)
- The RPC connection will now be closed if there's nothing to show
- Added a button to fast enable/disable rich presence per project
- Added Golo
- Added .htaccess
- Added .cxml and .fxml to XML
- Added Git
- Fixed ConcurrentModificationExceptions when running multiple instances

## v0.5.0 - Settings and more
- Added Vue.js
- Fixed file name change handling
- Fixed rare issue with locale settings (#13)
- Added YAML
- Updated Java-DiscordRPC to v1.3.1 and utilized it's new equals methods
- Implemented settings menu (#15)

## v0.4.0
- Fixed elapsed time
- Sync is now done after a small delay which will speed up the process if you open multiple files rapidly
- Fixed compatibility with Android Studio
- Added TypeScript (#8)
- Added Lua

## v0.3.0
- Fixed name for files without extension
- Added MPS
- Fixed C++ and C# asset names
- Added CMake as language
- Fixed NPE on older versions of CLion (#5)
- Updated Java-DiscordRPC to v1.3.0

## v0.2.0
- The IDE distribution and the language of the current file are now shown as images of the rich presence.

## v0.1.0
- Initial Release
