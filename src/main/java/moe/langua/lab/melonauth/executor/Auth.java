package moe.langua.lab.melonauth.executor;

import moe.langua.lab.melonauth.Init;
import moe.langua.lab.melonauth.runnable.GetSkinImage;
import moe.langua.lab.melonauth.utils.Verifier;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class Auth implements CommandExecutor {
    private Init instance;
    private ArrayList<Player> verifiedList;
    private Map<UUID, Boolean> authMap;
    private Map<UUID, Verifier> verifierMap;
    private Map<String, String> languageMap;

    public Auth(Init instance, ArrayList<Player> verifiedList, Map<UUID, Boolean> authMap, Map<UUID, Verifier> verifierMap, Map<String, String> languageMap) {
        this.instance = instance;
        this.verifiedList = verifiedList;
        this.authMap = authMap;
        this.verifierMap = verifierMap;
        this.languageMap = languageMap;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(languageMap.get("prefix") + languageMap.get("player_only"));
            return true;
        }

        Player sender = (Player) commandSender;
        if (instance.getWaitList().contains(sender)) {
            if (!verifierMap.containsKey(sender.getUniqueId())) {
                sender.sendMessage(languageMap.get("prefix") + languageMap.get("obtaining_information"));
                return true;
            }
            long waitedTime = System.currentTimeMillis() - verifierMap.get(sender.getUniqueId()).getTimestamp();
            if (waitedTime > 60 * 1000) {
                commandSender.sendMessage(languageMap.get("prefix") + languageMap.get("start_verification"));
                verifierMap.get(sender.getUniqueId()).setTimestamp(System.currentTimeMillis());
                new GetSkinImage(instance, languageMap, sender) {
                    @Override
                    public void run() {
                        super.run();
                        if (skinImage == null) return;
                        if (verifierMap.get(sender.getUniqueId()).verify(skinImage)) {
                            verifierMap.remove(sender.getUniqueId());
                            instance.getWaitList().remove(sender);
                            authMap.put(sender.getUniqueId(), false);
                            verifiedList.add(sender);
                            if (sender.getGameMode() == GameMode.SPECTATOR)
                                Bukkit.getScheduler().runTask(instance, () -> sender.setGameMode(GameMode.SURVIVAL));
                            sender.sendMessage(languageMap.get("prefix") + languageMap.get("success"));
                            return;
                        }
                        sender.sendMessage(languageMap.get("prefix") + languageMap.get("fail"));
                    }
                }.runTaskAsynchronously(instance);
                return true;
            }
            sender.sendMessage(languageMap.get("prefix") + instance.applyPlaceholder(languageMap.get("cool_down"), String.valueOf(60 - (int) (waitedTime / 1000))));
            return true;
        }
        commandSender.sendMessage(languageMap.get("prefix") + languageMap.get("no_need_to_auth"));
        return true;
    }
}
