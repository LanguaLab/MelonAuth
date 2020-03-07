package moe.langua.lab.auth.spigot.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import moe.langua.lab.auth.spigot.SpigotStartup;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class Transceiver implements PluginMessageListener {
    private SpigotStartup instance;
    private String serverUniqueID;

    public Transceiver(UUID serverUniqueID, SpigotStartup instance) {
        this.instance = instance;
        this.serverUniqueID = serverUniqueID.toString();
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {

        if (channel.equalsIgnoreCase(instance.MAIN_CHANNEL)) {
            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
            String serverUUID = in.readUTF();
            if(serverUUID.equalsIgnoreCase(serverUniqueID)) return;
            int action = in.readInt();
            UUID playerUniqueID = UUID.fromString(in.readUTF());
            switch (action){
                case 3:
                    instance.getVerifiedSet().add(playerUniqueID);
                    return;
                case 4:
                    instance.getOriginalGameModeMap().put(playerUniqueID,instance.getServer().getPlayer(playerUniqueID).getGameMode());
                    instance.getServer().getPlayer(playerUniqueID).setGameMode(GameMode.SPECTATOR);
                    return;
            }
        }

        if (channel.equalsIgnoreCase(instance.ANNOUNCE_CHANNEL)) {
            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
            int action = in.readInt();
            UUID playerUniqueID = UUID.fromString(in.readUTF());
            if(!instance.getServer().getPlayer(playerUniqueID).isOnline()) return;
            switch (action) {
                case 5:
                    instance.getVerifiedSet().add(playerUniqueID);
                    instance.getServer().getPlayer(playerUniqueID).setGameMode(instance.getOriginalGameModeMap().get(playerUniqueID));
                    instance.getOriginalGameModeMap().remove(playerUniqueID);
                    break;
            }
        }

    }

    public void sendPluginMessage(String serverUniqueID, int action, UUID uniqueID) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(serverUniqueID);
        out.writeInt(action);
        out.writeUTF(uniqueID.toString());
        instance.getServer().getPlayer(uniqueID).sendPluginMessage(instance, instance.MAIN_CHANNEL, out.toByteArray());
    }
}
