package moe.langua.lab.melonauth.listeners;

import moe.langua.lab.melonauth.Init;
import moe.langua.lab.melonauth.runnable.GetSkinImage;
import moe.langua.lab.melonauth.utils.Verifier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;


import java.io.*;
import java.net.URLEncoder;
import java.util.*;

public class AuthTrigger implements Listener {
    private Init instance;
    private ArrayList<Player> verifiedList;
    private Map<UUID, Boolean> authMap;
    private Map<String, String> languageMap;
    private Map<UUID, Verifier> verifierMap;

    public AuthTrigger(Init instance, ArrayList<Player> verifiedList, Map<UUID, Boolean> authMap, Map<UUID, Verifier> verifierMap, Map<String, String> languageMap) {
        this.instance = instance;
        this.verifiedList = verifiedList;
        this.authMap = authMap;
        this.verifierMap = verifierMap;
        this.languageMap = languageMap;
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        if (instance.isStrictMode()) {
            if (!authMap.containsKey(event.getUniqueId())) {
                authMap.put(event.getUniqueId(), true);
            }
            return;
        }
        if (!Bukkit.getOfflinePlayer(event.getUniqueId()).hasPlayedBefore()) {
            authMap.put(event.getUniqueId(), true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("melonauth.bypass") || authMap.get(event.getPlayer().getUniqueId()) != null && !authMap.get(event.getPlayer().getUniqueId())) {
            verifiedList.add(event.getPlayer());
            if (instance.hasUpdate() && event.getPlayer().hasPermission("melonauth.notice")) {
                event.getPlayer().sendMessage(languageMap.get("prefix") + instance.applyPlaceholder(languageMap.get("update_notice"), instance.getLatestVersion(), "https://github.com/LanguaLab/MelonAuth/releases/" + instance.getLatestVersion()));
            }
            return;
        }
        event.setJoinMessage("");
        if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) event.getPlayer().setGameMode(GameMode.SPECTATOR);
        instance.getWaitList().add(event.getPlayer());
        event.getPlayer().sendMessage(languageMap.get("join"));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + event.getPlayer().getName() + " " + instance.applyPlaceholder(languageMap.get("document.tellraw"), languageMap.get("document.notice"), languageMap.get("document.click_word"), languageMap.get("document.url"), languageMap.get("document.hover_notice")));
        new GetSkinImage(instance, languageMap, event.getPlayer()) {
            @Override
            public void run() {
                super.run();
                if (skinImage == null) return;
                Verifier verifier;
                try {
                    verifier = new Verifier(skinImage);
                    verifierMap.put(event.getPlayer().getUniqueId(), verifier);
                } catch (IOException e) {
                    long timestamp = System.currentTimeMillis();
                    System.out.println("It's an unexpected error. Please open a new issue at https://github.com/LanguaLab/MelonAuth/issues" +
                            "/n[Timestamp] " + timestamp);
                    e.printStackTrace();
                    event.getPlayer().sendMessage(instance.applyPlaceholder(languageMap.get("unexpected_error"), String.valueOf(timestamp)));
                    this.runTaskAsynchronously(instance);
                    return;
                }
                String skinPaintedBase64 = Base64.getEncoder().encodeToString(verifier.getPaintedSkin());
                String skinURL = null;
                try {
                    skinURL = instance.applyPlaceholder(languageMap.get("skin.url"),event.getPlayer().getName()+"-verification-"+System.currentTimeMillis(), URLEncoder.encode(skinPaintedBase64, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                final String finalSkinURL = skinURL;
                Bukkit.getScheduler().runTask(instance, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + event.getPlayer().getName() + " " + instance.applyPlaceholder(languageMap.get("skin.tellraw"), languageMap.get("skin.notice"), languageMap.get("skin.click_word"), finalSkinURL, languageMap.get("skin.hover_notice"))));
            }

        }.runTaskLaterAsynchronously(instance, 0L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        verifierMap.remove(event.getPlayer().getUniqueId());
        instance.getWaitList().remove(event.getPlayer());
        verifiedList.remove(event.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (instance.isFreeze() && verifiedList.indexOf(event.getPlayer()) == -1) event.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (instance.isMute() && verifiedList.indexOf(event.getPlayer()) == -1) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.DARK_GRAY + "§m<" + event.getPlayer().getName() + "> " + event.getMessage());
        }
    }

}
