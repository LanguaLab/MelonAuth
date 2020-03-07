package moe.langua.lab.auth.bungeecord.listeners;

import moe.langua.lab.auth.bungeecord.BungeeStartup;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class Command implements Listener {
    private BungeeStartup instance;

    public Command(BungeeStartup instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onCommand(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) return;
        if(event.getMessage().equalsIgnoreCase("/auth")) return;
        byte status = instance.getVerificationManager().getVerificationStatus(((ProxiedPlayer) event.getSender()).getUniqueId());
        if (status == (byte) 0x01 || (instance.getVerificationManager().isStrictMode() && status == (byte) 0x02)) {
            event.setCancelled(true);
            ((ProxiedPlayer) event.getSender()).sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(instance.getVerificationManager().getLanguage("need_to_verify")));
        }
    }
}
