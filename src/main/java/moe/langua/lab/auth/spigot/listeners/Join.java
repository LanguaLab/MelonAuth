package moe.langua.lab.auth.spigot.listeners;

import moe.langua.lab.auth.spigot.SpigotStartup;
import moe.langua.lab.auth.utils.MessageSender;
import moe.langua.lab.auth.utils.PlayerInstructor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.JSONException;

import java.io.IOException;

public class Join implements Listener {
    private SpigotStartup instance;

    public Join(SpigotStartup instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        if (!Bukkit.getOfflinePlayer(event.getUniqueId()).hasPlayedBefore())
            instance.getFirstJoinSet().add(event.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (instance.isClient()) {
            if (instance.getFirstJoinSet().contains(event.getPlayer().getUniqueId())) {
                instance.getServer().getScheduler().runTaskLater(instance, () -> {
                    instance.getTransceiver().sendPluginMessage(instance.getServerUniqueID().toString(), 1, event.getPlayer().getUniqueId());
                }, 5L);
                instance.getFirstJoinSet().remove(event.getPlayer().getUniqueId());
            } else {
                instance.getServer().getScheduler().runTaskLater(instance, () -> {
                    instance.getTransceiver().sendPluginMessage(instance.getServerUniqueID().toString(), 2, event.getPlayer().getUniqueId());
                }, 5L);
            }
            return;
        }
        byte status = instance.getVerificationManager().getVerificationStatus(event.getPlayer().getUniqueId());

        if (status == (byte) 0xFF) return;
        if (status == (byte) 0x00) {
            if (instance.getFirstJoinSet().contains(event.getPlayer().getUniqueId())) {
                instance.getVerificationManager().setVerificationStatus(event.getPlayer().getUniqueId(), (byte) 0x01);
                instance.getFirstJoinSet().remove(event.getPlayer().getUniqueId());
                status = (byte) 0x01;
            } else {
                instance.getVerificationManager().setVerificationStatus(event.getPlayer().getUniqueId(), (byte) 0x02);
                status = (byte) 0x02;
            }
        }
        if (status == (byte) 0x01 || (instance.getVerificationManager().isStrictMode() && status == (byte) 0x02)) {
            instance.getServer().getScheduler().runTaskAsynchronously(instance, () -> {
                try {
                    instance.lock(event.getPlayer().getUniqueId());
                    instance.refreshCoolDownTime(event.getPlayer().getUniqueId());
                    PlayerInstructor.start(new MessageSender(event.getPlayer()), instance.getVerificationManager());
                    instance.unlock(event.getPlayer().getUniqueId());
                } catch (InterruptedException | JSONException | IOException e) {
                    long timeStamp = System.currentTimeMillis();
                    instance.getLogger().warning("Unexpected error occurred. Timestamp:" + timeStamp);
                    e.printStackTrace();
                    event.getPlayer().spigot().sendMessage(new TextComponent(instance.getVerificationManager().getLanguage("unexpected_error", "" + timeStamp)));
                }
            });
        }
    }
}
