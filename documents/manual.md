# Verification Manual
This manual will helps you to finish the account verification. Prompts messages you will see may vary from server to server, but the ideas and the verification process will be the same.

## 1. What will happen when I login the server?

+ you may receive a message means `You have to verify your account to enjoy this server`.  
+ You will see the hyperlink text, which suggests you read this manual.
+ (You may receive a notice which means `Server cannot obtain the required resources now`, which caused by the usage limit of Mojang API. Don't worry about it, the server will wait for 20 seconds and try again automatically. You don't need to log out at this time. You may receive this message multiple times until the server successfully obtains the information).
+ After server obtain the necessary information(If the `cannot obtain the resources` doesn't appear, it usually only takes a few seconds), you will be sent a message contains a hyperlink to download your verification skin. Click the hyperlink text to download it. (You should use a modern browser to open the link, use Chrome, Firefox, Edge or Safari instead of Internet Explorer). Don't worry about the length of the super long link, it just as long as it should be.
+ The name of skin file is `YourName-verification-a_string_of_numbers.png`.

## 2. What should I do after I downloaded the skin?

+ Login minecraft.net or start Minecraft Launcher.
+ If you choose to login minecraft.net, enter [skin](https://my.minecraft.net/en-us/profile/skin) page after you log in, upload the skin file you downloaded from the server.
+ If you choose to start Minecraft Launcher, choose `skin` section at the main menu. Choose `New Skin`, and upload the skin file you downloaded from the server. Give a recognizable name to the verification skin and click `Save & Use` button.
+ You may be asked to provide additional account verification information(such as secret questions) at any of the skin uploading step(depend on your account activities).
+ You may forget the answer to your secret question. If so, please use the email which registered your account to reset them, and keep them in your mind.

## 3. What should I do after I uploaded my skin?

+ Use `/auth` command to submit the verification request.
+ You can only submit one verification request to the server per minute.
+ The Results will be returned to you through a message.
+ As the first step, you may receive a notice which means `server cannot obtain the required resources`, Please wait for a while and the server will try again automatically.
+ If you were sent a message which means `Wrong verification code detected`, make sure you have uploaded the correct skin. If you are sure that you uploaded the correct skin, please try again in a minute(it may be caused by the cache storage in Mojang session server not refreshed in time)