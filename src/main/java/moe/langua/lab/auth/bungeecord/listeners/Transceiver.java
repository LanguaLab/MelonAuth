package moe.langua.lab.auth.bungeecord.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import moe.langua.lab.auth.bungeecord.BungeeStartup;
import moe.langua.lab.auth.utils.MessageSender;
import moe.langua.lab.auth.utils.PlayerInstructor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.json.JSONException;

import java.io.IOException;
import java.util.UUID;


public class Transceiver implements Listener {
    private BungeeStartup instance;

    public void announce(int action, UUID uniqueID) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(action);
        out.writeUTF(uniqueID.toString());
        instance.getProxy().getPlayer(uniqueID).getServer().getInfo().sendData(instance.ANNOUNCE_CHANNEL, out.toByteArray());
    }

    public void sendPluginMessage(String serverUniqueID, int action, UUID uniqueID) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(serverUniqueID);
        out.writeInt(action);
        out.writeUTF(uniqueID.toString());
        instance.getProxy().getPlayer(uniqueID).getServer().getInfo().sendData(instance.MAIN_CHANNEL, out.toByteArray());
    }

    public Transceiver(BungeeStartup instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onMessageReceived(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase(instance.MAIN_CHANNEL)) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String serverUUID = in.readUTF();
        int actionID = in.readInt();
        UUID playerUUID = UUID.fromString(in.readUTF());

        byte status = instance.getVerificationManager().getVerificationStatus(playerUUID);

        if (status == (byte) 0xFF) {
            sendPluginMessage(serverUUID, 3, playerUUID);
            return;
        }

        if (status == (byte) 0x00) {
            switch (actionID) {
                case 1:
                    instance.getVerificationManager().setVerificationStatus(playerUUID, (byte) 0x01);
                    status = (byte) 0x01;
                    break;
                case 2:
                    instance.getVerificationManager().setVerificationStatus(playerUUID, (byte) 0x02);
                    status = (byte) 0x02;
                    break;
            }
        }

        if (status == (byte) 0x01 || (instance.getVerificationManager().isStrictMode() && status == (byte) 0x02)) {
            sendPluginMessage(serverUUID, 4, playerUUID);
            instance.getProxy().getScheduler().runAsync(instance,()->{
                try {
                    instance.lock(instance.getProxy().getPlayer(playerUUID).getUniqueId());
                    instance.refreshCoolDownTime(instance.getProxy().getPlayer(playerUUID).getUniqueId());
                    PlayerInstructor.start(new MessageSender(instance.getProxy().getPlayer(playerUUID)), instance.getVerificationManager());
                    instance.unlock(instance.getProxy().getPlayer(playerUUID).getUniqueId());
                } catch (InterruptedException | JSONException | IOException e) {
                    long timeStamp = System.currentTimeMillis();
                    instance.getLogger().warning("Unexpected error occurred. Timestamp:" + timeStamp);
                    e.printStackTrace();
                    instance.getProxy().getPlayer(playerUUID).sendMessage(new TextComponent(instance.getVerificationManager().getLanguage("unexpected_error", "" + timeStamp)));
                }
            });
        }

    }


}
