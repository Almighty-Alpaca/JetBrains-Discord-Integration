## v1.11.0
- Fix compatibility with IntelliJ version 2022.3

## v1.10.0
- Fix `Cannot create listener` exception (#230)
- Fix active time for projects (#237)
- Update Zig icon (#239)
- Update Dart icon (#214)

## v1.9.0
- Support IntelliJ version 2022.1 (#221)
- Add classic Godot icon (#222)

## v1.8.0
- Fix compatibility with IntelliJ version 2021.3 (#204)
- Add support for DataSpell (#205)
- Make some settings easier to understand (#206)

## v1.7.1
- Fix conflict with other plugins using the same project structure (e.g. Perl) (#192)
- Fix `Access is allowed from event dispatch thread only` error in commit view (#191)

## v1.7.0
- Add option to show a simple "Idling" message instead of hiding everything when afk (#163)
- Support for Apple Silicon (#166)
- Support for IntelliJ version 2021.2 (#159, #164)
- Enhance template documentation (#155)
- Better icon search order (#147) 
- Better error handling in templates

## v1.6.1
- Fix unneeded dependencies inflating plugin size 

## v1.6.0
- Implement custom templates
- Add support for .cjs and .mjs files
- Add option to hide  presence while a specific project is open
- Fix log spam by moving all repetitive logging to debug level
- Fix extensions conflicts
- Fix AlreadyDisposedExceptions
- Fix compilation on Java 8
- Fix ClassCastException when rendering preview with disabled presence
- Fix preview for default avatars

## v1.5.0
- Icon text is now more customizable
- Fix sporadic ProcessCanceledException when updating presence
- Fix invisible lines
- Fix resetting open time
- Fix read action required error when getting Git info
- Fix access error when getting current editor

## v1.4.1 & 1.4.2
- Fix error when the Git plugin is disabled
- Fix update notification spam
- Fix "null" branch when no vcs was found
- Reduce log spam
- Enable local icons and languages

## v1.4.0
- Add ability to ignore files in .gitignore
- Add option to show a project but hide files
- Add support for showing the time the IDe was active instead of total time since startup 
- Rework actions in toolbar
- Detect Discord running in Flatpak
- Enhance preview in settings
- Fix exception when checking for other Discord plugins on startup
- Fix idle timeout always being enabled

## v1.3.0
- Add a button to force reconnect to Discord client
- Add ability to show current git branch (#89)
- Add support for Rider for Unreal Engine preview
- Add support for idle timeout and elapsed time for files/projects again 
- Fix a lot of bugs that could prevent the plugin from loading
- Fix settings preview being rendered in the background
- Fix hidden projects being shown
- Change target Java version back to 1.8

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

## v0.5.0
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
