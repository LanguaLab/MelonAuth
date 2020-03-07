package moe.langua.lab.auth.bungeecord;

import moe.langua.lab.auth.bungeecord.commands.Auth;
import moe.langua.lab.auth.bungeecord.listeners.Transceiver;
import moe.langua.lab.auth.core.VerificationManager;
import moe.langua.lab.auth.utils.exceptions.PluginLoadFailedException;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashMap;
import java.util.UUID;

public class BungeeStartup extends Plugin {
    public final String MAIN_CHANNEL = "melonauth:main";
    public final String ANNOUNCE_CHANNEL = "melonauth:announce";

    private VerificationManager verificationManager;
    private HashMap<UUID, Long> timeStampMap = new HashMap<>();
    private HashMap<UUID, Boolean> taskLockMap = new HashMap<>();
    private Transceiver transceiver;

    @Override
    public void onEnable() {
        getProxy().registerChannel(MAIN_CHANNEL);
        getProxy().registerChannel(ANNOUNCE_CHANNEL);
        try {
            verificationManager = new VerificationManager(this.getDataFolder(), this.getLogger());
        } catch (PluginLoadFailedException e) {
            e.printStackTrace();
            this.getLogger().warning("Failed to load Plugin...Stopping the server...");
            this.getProxy().stop();
        }
        transceiver = new Transceiver(this);
        this.getProxy().getPluginManager().registerListener(this, transceiver);
        this.getProxy().getPluginManager().registerCommand(this, new Auth(this));
        //TODO add AutoSave task
    }

    @Override
    public void onDisable(){
        verificationManager.saveVerificationMap();
    }

    public VerificationManager getVerificationManager() {
        return verificationManager;
    }

    public Transceiver getTransceiver() {
        return transceiver;
    }

    public void refreshCoolDownTime(UUID uniqueID) {
        timeStampMap.put(uniqueID, System.currentTimeMillis());
    }

    public long isCooledDown(UUID uniqueID, long timeInMillSeconds) {
        long difference =  timeStampMap.get(uniqueID) + timeInMillSeconds - System.currentTimeMillis();
        return difference < 0 ? 0 : difference;

    }

    public void lock(UUID uniqueID){
        taskLockMap.put(uniqueID,true);
    }

    public void unlock(UUID uniqueID){
        taskLockMap.remove(uniqueID);
    }

    public boolean isLocked(UUID uniqueID){
        return taskLockMap.get(uniqueID);
    }
}
