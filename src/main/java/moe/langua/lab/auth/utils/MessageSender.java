package moe.langua.lab.auth.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MessageSender {
    private int workMode;
    private Object player;

    public MessageSender(Player player) {
        this.player = player;
        workMode = 0;
    }

    public MessageSender(ProxiedPlayer proxiedPlayer) {
        this.player = proxiedPlayer;
        workMode = 1;
    }

    public void sendMessage(String msg) {
        switch (workMode) {
            case 0:
                ((Player) player).spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(msg));
                break;
            case 1:
                ((ProxiedPlayer) player).sendMessage(ChatMessageType.CHAT, new TextComponent(msg));
                break;
        }
    }

    public void sendMessage(BaseComponent component) {
        switch (workMode) {
            case 0:
                ((Player) player).spigot().sendMessage(ChatMessageType.CHAT, component);
                break;
            case 1:
                ((ProxiedPlayer) player).sendMessage(ChatMessageType.CHAT, component);
                break;
        }
    }

    public void sendActionBar(String msg) {
        switch (workMode) {
            case 0:
                ((Player) player).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
                break;
            case 1:
                ((ProxiedPlayer) player).sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
                break;
        }
    }

    public void sendActionBar(BaseComponent component) {
        switch (workMode) {
            case 0:
                ((Player) player).spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
                break;
            case 1:
                ((ProxiedPlayer) player).sendMessage(ChatMessageType.ACTION_BAR, component);
                break;
        }
    }

    public UUID getUUID(){
        switch (workMode) {
            case 0:
                return ((Player) player).getUniqueId();
            case 1:
                return ((ProxiedPlayer) player).getUniqueId();
            default:
                return null;
        }
    }

    public boolean isOnline(){
        switch (workMode) {
            case 0:
                return ((Player) player).isOnline();
            case 1:
                return ((ProxiedPlayer) player).isConnected();
            default:
                return false;
        }
    }
}
