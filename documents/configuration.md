# Configuration Manual
Functions can be configured in `plugins/MelonAuth/config.yml`. This file will be generated at the first startup.
The language file can be found in `plugins/MelonAuth/LOCALE.yml`. English and Chinese language files will be generated at first setup. You can modify them as you need. If you can translate them to your language, feel free to [pull request](https://github.com/LanguaLab/MelonAuth/pulls), share your translation to more people.

## Config.yml

```
Version: 1
Language: en_US
AutoSave: 60
StrictMode: false
Freeze: true
Mute: true
```
Here is the example of the `config.yml`, the usage of every item will be listed below.

### Version
`Version` is an item for version control, it helps MelonAuth update your config file with new items automatically. Please **DO NOT** change the value of this item by yourself.

### Language
`Language` is an item for language control. You can choose a language you want to use here. The language file `{$Language}.yml` will be loaded.  
**e.g.** If your `Language` setting is `en_US`, `en_US.yml` will be loaded as the language file.

### AutoSave
`Autosave` is an item to control the autosave interval(in seconds) of the data file(`auth.data`).  
**e.g** If your `AutoSave` setting is `60`, the verification data will be saved in `auth.data` file per minute automatically.

### StrictMode
`StrictMode` determines whether **old** players(joined before the plugin installed) will be asked to verify their account. `StrictMode` can be flexibly adjusted without any data loss. All the verified players will not be asked to verify their account again although you changed the value of this item.

### Freeze
`Freeze` is a setting to control whether a player will be freeze when they are not verified their account.

### Mute
`Mute` is a setting to control whether a player will be mute when they are not verified their account.

## Language File
The default language files will be generated at the first setup(include `en_US.yml` and `zh_CN.yml`). Language files will be saved in `plugins/MelonAuth/` folder.  
You can create language files with any name as you like. After completing your custom language file, change the language to your language file name (without `.yml` suffix).  
**e.g** Your custom language file named `my_language.yml`, you should set `Language` in `config.yml` to `my_language`.

### Version
Language file will also contains an item named `Version`, it helps MelonAuth update your Language file automatically. Please **DO NOT** change the value of this item by yourself.
Language file upgrade will add new items to language files. These new items will be added in English by default.

## Commands

MelonAuth has two sample commands:
+ auth
+ reload

#### /auth
`/auth` is a command for players to submit verification request to the server only(can't be used as a console). Will be used when a player after completed the verification preparation. No permission needed to use this command.

#### /reload
`/melonauth:reload`(because using `/reload` will conflict with the original `/spigot:reload` command, please use `/melonauth:reload` instead.) command will reload all the configration of MelonAuth plugin(includes `config.yml` and language file). Sender of this command should have `melonauth.reload` permission.

## Permissions
All the permissions MelonAuth may ask for will be listed below:
+ melonauth.bypass
+ melonauth.notice
+ melonauth.reload

### melonauth.bypass
If a player has this permission, MelonAuth will not ask for account verification to this player.  
One thing needs special attention: melonauth.bypass permission can only let the player bypass the verification temporary. If an unverified player lost `melonauth.bypass` permission one day, the player will be treated as an `unverified old player`. If `StrictMode` is enabled, that player will be asked for verification on the next login.

### melonauth.notice
If a player has this permission, plugin update notice will be sent to the player when the player login server(if update available).

### melonauth.reload
If a player wants to use `/melonauth:reload` command, the player must have permission `melonauth.reload`. Or the player will receive a `permission denied` message.

## Bstats
MelonAuth use `Bstats` to collect your usage statistics. This can help us to make MelonAuth better.
