# MelonAuth

![banner_zh](https://raw.githubusercontent.com/LanguaLab/MelonAuth/master/image/banner_zh.png)

MelonAuth 是一个可以帮助您 Minecraft 账户合法性的 Spigot 插件。它可以帮助您的服务器远离使用非法账户的玩家。

## 为什么选择 MelonAuth?

+ 全自动化。
+ 不需要询问邮箱或密码。
+ 使用简单。
+ 配置方便。

## 如何进行验证?
当新玩家加入服务器时，MelonAuth 将会生成一个 192 bit 长的随机验证码，并将其打印在玩家皮肤文件的左上角。
玩家需要做的是下载此皮肤文件，登录 minecraft.net（或Minecraft启动器）上传，并将其作为新皮肤。  
当玩家上传新皮肤时，minecraft.net 将会选择是否让此玩家回答密保问题(取决于此玩家的账户活动情况)。通常情况下，使用非法账户的玩家在这一步会遇到困难，因为此玩家将不知道账户原主人设定的密保问题答案。  
简单来讲，**MelonAuth 将使用 Mojang 账户系统的原生账户验证来验证账户的合法性。**  

## Q & A (关于玩家)

### MelonAuth 将在什么时候验证我的账户？
当新玩家加入服务器时，MelonAuth 将自动开始账户验证。
关于玩家如何完成验证，请您阅读[验证手册](https://github.com/LanguaLab/MelonAuth/blob/master/documents/manual_zh.md)。

### 我不想向你提供任何隐私信息。
MelonAuth **不会**向您请求任何隐私信息。所有可能出现的隐私信息将在您与Mojang之间直接传输。这些信息将不会被任何第三方收集(包括您将要加入的服务器)。

### 别弄脏我的衣服了！
请不要担心您的衣服。验证码打印的位置将在游戏中不可见。您原来的皮肤与打印过验证码的皮肤在游戏中看起来的效果将是一样的。所以，您可以随意地将验证码保持在身上(事实上您的身体看起来和原来没有什么不同)，**完成验证后，您将不需要进行进一步操作** *（例如换回原来的皮肤）*。


## 现在获取 MelonAuth
您可以选择下面两种方式的一种来获取 MelonAuth 插件。
+ 从 [Release](https://github.com/LanguaLab/MelonAuth/releases) 页面下载。
+ 自己构建。（请参考 [构建](https://github.com/LanguaLab/MelonAuth/blob/master/documents/README_zh.md#%E6%9E%84%E5%BB%BA)）

## 配置
+ 请阅读 [configuration.md](https://github.com/LanguaLab/MelonAuth/blob/master/documents/configuration.md)（未汉化）。

## 开发者相关

### 构建

想要构建 MelonAuth，您需要先安装 [maven](https://maven.apache.org)。

1. 复制git库到本地。
```
$ git clone git@github.com:LanguaLab/MelonAuth.git
```
您可能更喜欢使用 https:
```
$ git clone https://github.com/LanguaLab/MelonAuth.git
```
您也可以不使用git下载此仓库。（[下载压缩包](https://github.com/LanguaLab/MelonAuth/archive/master.zip)）

2. 使用 maven 构建插件。
```
$ cd $PATH_TO_REPOSITORY
$ maven clean install
```
构建好的插件将被放在 `$PATH_TO_REPOSITORY/target/melonauth-{$version}.jar`。

## 问题追踪
感谢您使用 MelonAuth 。如果您在使用过程中，发现它并不那么完美，希望您能与我们取得联系，将您的困惑告诉我们。  
我们随时欢迎您[提交新issue](https://github.com/LanguaLab/MelonAuth/issues)。中文与英语均可用。


