package moe.langua.lab.auth.bungeecord.commands;

import moe.langua.lab.auth.bungeecord.BungeeStartup;
import moe.langua.lab.auth.utils.MessageSender;
import moe.langua.lab.auth.utils.SkinGetter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.json.JSONException;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class Auth extends Command {
    private BungeeStartup instance;

    public Auth(BungeeStartup instance) {
        super("auth");
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new TextComponent(instance.getVerificationManager().getLanguage("prefix") + instance.getVerificationManager().getLanguage("player_only")));
            return;
        }
        ProxiedPlayer sender = (ProxiedPlayer) commandSender;
        if (!instance.getVerificationManager().isVerifying(sender.getUniqueId())) {
            commandSender.sendMessage(new TextComponent(instance.getVerificationManager().getLanguage("prefix") + instance.getVerificationManager().getLanguage("no_need_to_auth")));
            return;
        }
        if (instance.isLocked(sender.getUniqueId())) {
            commandSender.sendMessage(new TextComponent(instance.getVerificationManager().getLanguage("prefix") + instance.getVerificationManager().getLanguage("task_running")));
            return;
        }
        long cooldown = instance.isCooledDown(sender.getUniqueId(), 6000);
        if (cooldown != 0) {
            commandSender.sendMessage(new TextComponent(instance.getVerificationManager().getLanguage("prefix") + instance.getVerificationManager().getLanguage("cooling_down", "" + cooldown / 1000)));
            return;
        }
        instance.getProxy().getScheduler().runAsync(instance, () -> {
            instance.lock(sender.getUniqueId());
            BufferedImage skinImage;
            try {
                skinImage = SkinGetter.getSkin(new MessageSender(sender), instance.getVerificationManager());
            } catch (JSONException | InterruptedException | IOException e) {
                long timeStamp = System.currentTimeMillis();
                instance.getLogger().warning("Unexpected error occurred. Timestamp:" + timeStamp);
                e.printStackTrace();
                sender.sendMessage(new TextComponent(instance.getVerificationManager().getLanguage("unexpected_error", "" + timeStamp)));
                return;
            }
            boolean success = instance.getVerificationManager().getVerifier(sender.getUniqueId()).verify(skinImage);
            if (success) {
                instance.getVerificationManager().setVerificationStatus(sender.getUniqueId(), (byte) 0xFF);
                instance.getTransceiver().announce(3, sender.getUniqueId());
                sender.sendMessage(new TextComponent(instance.getVerificationManager().getLanguage("prefix") + instance.getVerificationManager().getLanguage("success")));
            } else {
                instance.refreshCoolDownTime(sender.getUniqueId());
                sender.sendMessage(new TextComponent(instance.getVerificationManager().getLanguage("prefix") + instance.getVerificationManager().getLanguage("fail")));
            }
        });
    }
}
