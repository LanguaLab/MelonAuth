package moe.langua.lab.auth.utils;

import moe.langua.lab.auth.bungeecord.BungeeStartup;
import moe.langua.lab.auth.core.VerificationManager;
import moe.langua.lab.auth.spigot.SpigotStartup;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.json.JSONException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;

public class PlayerInstructor {
    public static void start(MessageSender sender, VerificationManager verificationManager) throws InterruptedException, JSONException, IOException {
        sender.sendMessage(verificationManager.getLanguage("notice.join"));

        TextComponent clickNotice;
        TextComponent clickWord;

        clickNotice = new TextComponent(verificationManager.getLanguage("document.notice"));
        clickWord = new TextComponent(verificationManager.getLanguage("document.click_word"));
        clickWord.setBold(true);
        clickWord.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(verificationManager.getLanguage("document.hover_notice"))}));
        clickWord.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, verificationManager.getLanguage("document.url")));
        clickNotice.addExtra(clickWord);
        sender.sendMessage(clickNotice);

        BufferedImage skin = SkinGetter.getSkin(sender, verificationManager);
        verificationManager.setVerifier(sender.getUUID(), new Verifier(skin));

        clickNotice = new TextComponent(verificationManager.getLanguage("skin.notice"));
        clickWord = new TextComponent(verificationManager.getLanguage("skin.click_word"));
        clickWord.setBold(true);
        clickWord.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(verificationManager.getLanguage("skin.hover_notice"))}));
        clickWord.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, verificationManager.getLanguage("skin.url", "" + verificationManager.getVerifier(sender.getUUID()).getSeed(), Base64.getEncoder().encodeToString(verificationManager.getVerifier(sender.getUUID()).getPaintedSkin()))));
        clickNotice.addExtra(clickWord);
        sender.sendMessage(clickNotice);
    }
}
