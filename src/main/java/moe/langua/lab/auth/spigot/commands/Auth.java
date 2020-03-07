package moe.langua.lab.auth.spigot.commands;

import moe.langua.lab.auth.spigot.SpigotStartup;
import moe.langua.lab.auth.utils.MessageSender;
import moe.langua.lab.auth.utils.SkinGetter;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONException;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class Auth implements CommandExecutor {
    SpigotStartup instance;

    public Auth(SpigotStartup instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.spigot().sendMessage(new TextComponent(instance.getVerificationManager().getLanguage("prefix") + instance.getVerificationManager().getLanguage("player_only")));
            return true;
        }
        Player sender = (Player) commandSender;
        if (!instance.getVerificationManager().isVerifying(sender.getUniqueId())) {
            commandSender.spigot().sendMessage(new TextComponent(instance.getVerificationManager().getLanguage("prefix") + instance.getVerificationManager().getLanguage("no_need_to_auth")));
            return true;
        }
        if (instance.isLocked(sender.getUniqueId())) {
            commandSender.spigot().sendMessage(new TextComponent(instance.getVerificationManager().getLanguage("prefix") + instance.getVerificationManager().getLanguage("task_running")));
            return true;
        }
        long cooldown = instance.isCooledDown(sender.getUniqueId(), 6000);
        if (cooldown != 0) {
            commandSender.spigot().sendMessage(new TextComponent(instance.getVerificationManager().getLanguage("prefix") + instance.getVerificationManager().getLanguage("cooling_down", "" + cooldown / 1000)));
            return true;
        }
        instance.getServer().getScheduler().runTaskAsynchronously(instance, () -> {
            instance.lock(sender.getUniqueId());
            BufferedImage skinImage;
            try {
                skinImage = SkinGetter.getSkin(new MessageSender(sender), instance.getVerificationManager());
            } catch (JSONException | InterruptedException | IOException e) {
                long timeStamp = System.currentTimeMillis();
                instance.getLogger().warning("Unexpected error occurred. Timestamp:" + timeStamp);
                e.printStackTrace();
                sender.spigot().sendMessage(new TextComponent(instance.getVerificationManager().getLanguage("unexpected_error", "" + timeStamp)));
                return;
            }
            boolean success = instance.getVerificationManager().getVerifier(sender.getUniqueId()).verify(skinImage);
            if (success) {
                instance.getVerificationManager().setVerificationStatus(sender.getUniqueId(), (byte) 0xFF);
                instance.getVerifiedSet().add(sender.getUniqueId());
                sender.spigot().sendMessage(new TextComponent(instance.getVerificationManager().getLanguage("prefix") + instance.getVerificationManager().getLanguage("success")));
            } else {
                instance.refreshCoolDownTime(sender.getUniqueId());
                sender.spigot().sendMessage(new TextComponent(instance.getVerificationManager().getLanguage("prefix") + instance.getVerificationManager().getLanguage("fail")));
            }
        });
        return true;
    }
}
