package moe.langua.lab.auth.spigot;

import moe.langua.lab.auth.core.VerificationManager;
import moe.langua.lab.auth.spigot.listeners.Freezer;
import moe.langua.lab.auth.spigot.listeners.Join;
import moe.langua.lab.auth.spigot.listeners.Transceiver;
import moe.langua.lab.auth.utils.exceptions.PluginLoadFailedException;
import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class SpigotStartup extends JavaPlugin {
    private boolean isClient;
    private VerificationManager verificationManager;
    private HashSet<UUID> firstJoinSet = new HashSet<>();
    private HashSet<UUID> verifiedSet = new HashSet<>();
    private HashMap<UUID, Long> timeStampMap = new HashMap<>();
    private HashMap<UUID, Boolean> taskLockMap = new HashMap<>();
    private HashMap<UUID, GameMode> originalGameModeMap = new HashMap<>();
    private UUID serverUniqueID = UUID.randomUUID();
    private Transceiver transceiver = new Transceiver(serverUniqueID,this);

    public final String MAIN_CHANNEL = "melonauth:main";
    public final String ANNOUNCE_CHANNEL = "melonauth:announce";

    public HashSet<UUID> getFirstJoinSet() {
        return firstJoinSet;
    }

    public HashSet<UUID> getVerifiedSet() {
        return verifiedSet;
    }

    public boolean isClient(){
        return isClient;
    }

    public HashMap<UUID, GameMode> getOriginalGameModeMap() {
        return originalGameModeMap;
    }

    public Transceiver getTransceiver(){
        return transceiver;
    }

    public UUID getServerUniqueID(){
        return serverUniqueID;
    }

    @Override
    public void onEnable(){
        isClient = !this.getServer().getOnlineMode();
        this.getServer().getPluginManager().registerEvents(new Freezer(this),this);
        this.getServer().getPluginManager().registerEvents(new Join(this),this);

        if(isClient){
            this.getLogger().info("Offline mode detected...Preparing to work with BungeeCord.");
            transceiver = new Transceiver(serverUniqueID,this);
            this.getServer().getMessenger().registerOutgoingPluginChannel(this,MAIN_CHANNEL);
            this.getServer().getMessenger().registerIncomingPluginChannel(this,MAIN_CHANNEL,transceiver);
            this.getServer().getMessenger().registerIncomingPluginChannel(this,ANNOUNCE_CHANNEL,transceiver);
            return;
        }

        try {
            verificationManager = new VerificationManager(this.getDataFolder(),this.getLogger());
        } catch (PluginLoadFailedException e) {
            e.printStackTrace();
            this.getLogger().warning("Failed to load Plugin...Stopping the server...");
            this.getServer().shutdown();
        }

        //TODO add AutoSave task.
    }

    @Override
    public void onDisable(){
        if(isClient) return;
        verificationManager.saveVerificationMap();
    }

    public VerificationManager getVerificationManager() {
        return verificationManager;
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
