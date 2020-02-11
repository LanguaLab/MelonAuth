package moe.langua.lab.melonauth.executor;

import moe.langua.lab.melonauth.Init;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class Reload implements CommandExecutor {
    private Init instance;
    private Map<String, String> languageMap;

    public Reload(Init instance, Map<String, String> languageMap) {
        this.instance = instance;
        this.languageMap = languageMap;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("melonauth.reload")) {
            commandSender.sendMessage(languageMap.get("prefix") + languageMap.get("permission_denied"));
            return true;
        }
        instance.reload();
        commandSender.sendMessage(languageMap.get("prefix") + languageMap.get("reloaded"));
        return true;
    }
}
