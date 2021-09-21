# MelonAuth

![banner](https://raw.githubusercontent.com/LanguaLab/MelonAuth/master/image/banner.png)

MelonAuth is a spigot plugin which helps you to verify the legality of a Minecraft account. It can help you to prevent players who is using illegal accounts from your server.

##### 如果你在寻找中文文档，请点击[这里](https://github.com/LanguaLab/MelonAuth/wiki/%E5%86%99%E5%9C%A8%E5%89%8D%E9%9D%A2)。
##### [MelonAuth 2 Plugin](https://github.com/Lori3f6/MelonAuth2-plugin)

## Why MelonAuth?

+ Full automation.
+ No need to ask for email or password.
+ Easy to configure & use.

## How to?
Different with other account verification plugins, MelonAuth will NOT ask for the player for their email or passwords.  
When a new players joins in the server, MelonAuth will generate a 192 bit long random verification code, convert it to 8x8 color blocks and paint it on the top-left corner of the player's skin image.  
What the player needs to do is to download the painted skin image, login minecraft.net(or Minecraft Launcher) and upload it as their new skin.  
When the player uploads their new skin, minecraft.net will choose whether or not to ask the player for additional verification information(depend on the account activities of this account). Generally, the user of illegal account will fall in difficulties at this step, because can't complete the additional verification challenge, which were set by the original owner of the account.
Simply speaking, **MelonAuth will verify the legality of the Minecraft account through the original security verification of the Mojang account system.**  

## Q & A (For Server Maintainers)
### 

## Q & A (For Players)

### When will MelonAuth verify my account?
MelonAuth will start account verification automatically when a new player login server.
To know how the players complete the verification, please read [Verification Manual](https://github.com/LanguaLab/MelonAuth/wiki/Verification-Manual).

### I Don't want to provide any private information to you.
MelonAuth will **NOT** request any privacy information from you. All private information that may be required will be transmitted directly between you and Mojang. It will not be collected by any third-party(include the server you may want to join).

### Don't stain my shirt!
Don't worry about your shirt. The place where the verification code painted is invisible in the game. The original skin and painted skin will look no different in the actual visual effects. So, you can just keep the verification code on your body(actually there are nothing different looks on your body), **no further action needed after the verification** *(such as change back to the original skin)*.


## Get it now
To get MelonAuth plugin, you can choose one of the following two methods:
+ Download from [Release](https://github.com/LanguaLab/MelonAuth/releases) Page.
+ Build by yourself. (see [Build](https://github.com/LanguaLab/MelonAuth#build))

## Configuration
+ See [Configuration Manual](https://github.com/LanguaLab/MelonAuth/wiki/Configuration-Manual)

## For developer

### Build

To build MelonAuth plugin, you need to have [maven](https://maven.apache.org) installed on your system. 

1. Clone the git repository.
```
$ git clone git@github.com:LanguaLab/MelonAuth.git
```
You may prefer to use https:
```
$ git clone https://github.com/LanguaLab/MelonAuth.git
```
You can also download the repository without git. ([Download zip](https://github.com/LanguaLab/MelonAuth/archive/master.zip)) 

2. Using maven to build the plugin.
```
$ cd $PATH_TO_REPOSITORY
$ maven clean install
```
The built plugin will be placed in `$PATH_TO_REPOSITORY/target/melonauth-${version}.jar`.

## Issue Tracker
We hope you can get in touch with us and let us know your confusion If you fall into any problems in use.  
Feel free to [open a new issue](https://github.com/LanguaLab/MelonAuth/issues). English and Chinese are both available.


