package moe.langua.lab.auth.spigot.listeners;

import moe.langua.lab.auth.spigot.SpigotStartup;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class Freezer implements Listener {
    private SpigotStartup instance;

    public Freezer(SpigotStartup instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        if(!instance.getVerifiedSet().contains(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event){
        if(instance.getVerifiedSet().contains(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(instance.getVerificationManager().getLanguage("need_to_verify")));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event){
        if(event.getMessage().indexOf("/auth")==0) return;
        if(!instance.getVerifiedSet().contains(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(instance.getVerificationManager().getLanguage("need_to_verify")));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDropItem(PlayerDropItemEvent event){
        if(!instance.getVerifiedSet().contains(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(instance.getVerificationManager().getLanguage("need_to_verify")));
        }
    }
}
